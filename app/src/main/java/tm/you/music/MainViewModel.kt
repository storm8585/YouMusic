package tm.you.music

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import tm.you.music.core.media.player.MusicPlayer
import tm.you.music.core.model.PlaybackState
import tm.you.music.core.model.Song
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = musicPlayer.playbackState

    fun playSong(song: Song) {
        musicPlayer.playSong(song)
    }

    fun setQueue(songs: List<Song>, startIndex: Int = 0) {
        musicPlayer.setQueue(songs, startIndex)
    }

    fun togglePlayPause() {
        if (musicPlayer.playbackState.value.isPlaying) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
    }

    fun skipNext() {
        musicPlayer.skipNext()
    }

    fun skipPrevious() {
        musicPlayer.skipPrevious()
    }

    fun seekTo(positionMs: Long) {
        musicPlayer.seekTo(positionMs)
    }
}
