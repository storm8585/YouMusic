package tm.you.music.core.media.player

import kotlinx.coroutines.flow.StateFlow
import tm.you.music.core.model.PlaybackState
import tm.you.music.core.model.Song

interface MusicPlayer {
    val playbackState: StateFlow<PlaybackState>
    val currentQueue: StateFlow<List<Song>>
    val isShuffle: StateFlow<Boolean>
    val isRepeat: StateFlow<Boolean>

    fun playSong(song: Song)
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun stop()
    fun setQueue(songs: List<Song>, startIndex: Int = 0)
    fun skipNext()
    fun skipPrevious()
    fun toggleShuffle()
    fun toggleRepeat()
}
