package tm.you.music.core.model

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String? = null
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artworkUrl: String? = null,
    val year: Int? = null
)

data class AudioStream(
    val url: String,
    val bitrate: Int,
    val format: String,
    val expirationTimestamp: Long? = null
)

data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false
)
