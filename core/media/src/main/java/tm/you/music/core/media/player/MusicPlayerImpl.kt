package tm.you.music.core.media.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tm.you.music.core.media.service.MusicService
import tm.you.music.core.model.PlaybackState
import tm.you.music.core.model.Song
import tm.you.music.core.network.MusicDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MusicPlayerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
    private val musicDataSource: MusicDataSource
) : MusicPlayer {

    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var progressJob: Job? = null
    private var currentSongItem: Song? = null
    private var currentQueueIndex: Int = -1

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentQueue = MutableStateFlow<List<Song>>(emptyList())
    override val currentQueue: StateFlow<List<Song>> = _currentQueue.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    override val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _isRepeat = MutableStateFlow(false)
    override val isRepeat: StateFlow<Boolean> = _isRepeat.asStateFlow()

    init {
        setupPlayerListener()
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
                if (isPlaying) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playbackState.value = _playbackState.value.copy(isBuffering = true)
                    }
                    Player.STATE_READY -> {
                        _playbackState.value = _playbackState.value.copy(
                            isBuffering = false,
                            durationMs = exoPlayer.duration.coerceAtLeast(0L)
                        )
                    }
                    Player.STATE_ENDED -> {
                        handleSongEnded()
                    }
                    Player.STATE_IDLE -> {
                        _playbackState.value = _playbackState.value.copy(isBuffering = false)
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playbackState.value = _playbackState.value.copy(
                    isBuffering = false,
                    isPlaying = false
                )
                // Attempt next track on persistent stream failure
                skipNext()
            }
        })
    }

    private fun handleSongEnded() {
        if (_isRepeat.value) {
            exoPlayer.seekTo(0L)
            exoPlayer.play()
        } else {
            skipNext()
        }
    }

    override fun playSong(song: Song) {
        val queue = _currentQueue.value
        val existingIndex = queue.indexOfFirst { it.id == song.id }
        if (existingIndex != -1) {
            currentQueueIndex = existingIndex
        } else {
            val newQueue = queue.toMutableList().apply { add(song) }
            _currentQueue.value = newQueue
            currentQueueIndex = newQueue.lastIndex
        }

        executePlay(song)
    }

    override fun setQueue(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        _currentQueue.value = songs
        val validIndex = startIndex.coerceIn(0, songs.lastIndex)
        currentQueueIndex = validIndex
        executePlay(songs[validIndex])
    }

    private fun executePlay(song: Song) {
        currentSongItem = song
        _playbackState.value = PlaybackState(
            currentSong = song,
            isPlaying = true,
            isBuffering = true,
            currentPositionMs = 0L,
            durationMs = song.durationMs
        )

        startForegroundService()

        playerScope.launch {
            try {
                val stream = musicDataSource.getStreamUrl(song.id)
                if (stream.url.isNotEmpty()) {
                    val mediaMetadata = MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .apply {
                            if (!song.artworkUrl.isNullOrEmpty()) {
                                setArtworkUri(Uri.parse(song.artworkUrl))
                            }
                        }
                        .build()

                    val mediaItem = MediaItem.Builder()
                        .setUri(stream.url)
                        .setMediaMetadata(mediaMetadata)
                        .build()

                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                } else {
                    _playbackState.value = _playbackState.value.copy(
                        isBuffering = false,
                        isPlaying = false
                    )
                }
            } catch (e: Exception) {
                _playbackState.value = _playbackState.value.copy(
                    isBuffering = false,
                    isPlaying = false
                )
            }
        }
    }

    override fun skipNext() {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return

        val nextIndex = if (_isShuffle.value && queue.size > 1) {
            var randomIdx: Int
            do {
                randomIdx = Random.nextInt(queue.size)
            } while (randomIdx == currentQueueIndex)
            randomIdx
        } else {
            (currentQueueIndex + 1) % queue.size
        }

        currentQueueIndex = nextIndex
        executePlay(queue[nextIndex])
    }

    override fun skipPrevious() {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (exoPlayer.currentPosition > 3000L) {
            exoPlayer.seekTo(0L)
            return
        }

        val prevIndex = if (_isShuffle.value && queue.size > 1) {
            var randomIdx: Int
            do {
                randomIdx = Random.nextInt(queue.size)
            } while (randomIdx == currentQueueIndex)
            randomIdx
        } else {
            if (currentQueueIndex - 1 < 0) queue.lastIndex else currentQueueIndex - 1
        }

        currentQueueIndex = prevIndex
        executePlay(queue[prevIndex])
    }

    override fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    override fun toggleRepeat() {
        _isRepeat.value = !_isRepeat.value
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun resume() {
        if (exoPlayer.playbackState == Player.STATE_IDLE && currentSongItem != null) {
            executePlay(currentSongItem!!)
        } else {
            exoPlayer.play()
        }
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    override fun stop() {
        exoPlayer.stop()
        stopProgressTracking()
        _playbackState.value = PlaybackState()
        currentSongItem = null
        currentQueueIndex = -1
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = playerScope.launch {
            while (isActive) {
                updatePlaybackState()
                delay(1000)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updatePlaybackState() {
        _playbackState.value = _playbackState.value.copy(
            currentSong = currentSongItem,
            isPlaying = exoPlayer.isPlaying,
            currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L),
            durationMs = exoPlayer.duration.coerceAtLeast(0L)
        )
    }

    private fun startForegroundService() {
        try {
            val intent = Intent(context, MusicService::class.java)
            ContextCompat.startForegroundService(context, intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
