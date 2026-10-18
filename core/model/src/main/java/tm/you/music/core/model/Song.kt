package tm.you.music.core.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val durationMs: Long = 0L,
    val artworkUrl: String? = null,
    val streamUrl: String? = null
)
