package tm.you.music.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tm.you.music.core.database.dao.SongDao
import tm.you.music.core.database.model.asEntity
import tm.you.music.core.media.player.MusicPlayer
import tm.you.music.core.model.PlaybackState
import tm.you.music.core.model.Song
import javax.inject.Inject

data class PlayerUiState(
    val playbackState: PlaybackState = PlaybackState(),
    val isFavorite: Boolean = false,
    val isShuffleEnabled: Boolean = false,
    val isRepeatEnabled: Boolean = false,
    val queue: List<Song> = emptyList(),
    val isQueueExpanded: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer,
    private val songDao: SongDao
) : ViewModel() {

    private val isQueueExpandedFlow = MutableStateFlow(false)

    private val isFavoriteFlow: StateFlow<Boolean> = musicPlayer.playbackState
        .map { it.currentSong?.id }
        .distinctUntilChanged()
        .flatMapLatest { songId ->
            if (songId != null) {
                songDao.isFavorite(songId)
            } else {
                flowOf(false)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val uiState: StateFlow<PlayerUiState> = combine(
        musicPlayer.playbackState,
        musicPlayer.currentQueue,
        musicPlayer.isShuffle,
        musicPlayer.isRepeat,
        isFavoriteFlow,
        isQueueExpandedFlow
    ) { playback, queue, shuffle, repeat, fav, queueExpanded ->
        PlayerUiState(
            playbackState = playback,
            queue = queue,
            isShuffleEnabled = shuffle,
            isRepeatEnabled = repeat,
            isFavorite = fav,
            isQueueExpanded = queueExpanded
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUiState()
    )

    fun playSong(song: Song) {
        musicPlayer.playSong(song)
    }

    fun togglePlayPause() {
        if (musicPlayer.playbackState.value.isPlaying) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
    }

    fun seekTo(positionMs: Long) {
        musicPlayer.seekTo(positionMs)
    }

    fun skipNext() {
        musicPlayer.skipNext()
    }

    fun skipPrevious() {
        musicPlayer.skipPrevious()
    }

    fun toggleFavorite() {
        val currentSong = musicPlayer.playbackState.value.currentSong ?: return
        val currentFav = isFavoriteFlow.value
        viewModelScope.launch(Dispatchers.IO) {
            if (currentFav) {
                songDao.deleteSong(currentSong.id)
            } else {
                songDao.insertOrUpdateSong(currentSong.asEntity(isFavorite = true))
            }
        }
    }

    fun toggleShuffle() {
        musicPlayer.toggleShuffle()
    }

    fun toggleRepeat() {
        musicPlayer.toggleRepeat()
    }

    fun toggleQueueExpansion() {
        isQueueExpandedFlow.value = !isQueueExpandedFlow.value
    }
}
