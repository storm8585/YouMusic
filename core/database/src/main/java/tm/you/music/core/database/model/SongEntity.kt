package tm.you.music.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import tm.you.music.core.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Long,
    val artworkUrl: String?,
    val isFavorite: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

fun SongEntity.asExternalModel(): Song = Song(
    id = id,
    title = title,
    artist = artist,
    album = album,
    durationMs = durationMs,
    artworkUrl = artworkUrl
)

fun Song.asEntity(isFavorite: Boolean = false): SongEntity = SongEntity(
    id = id,
    title = title,
    artist = artist,
    album = album,
    durationMs = durationMs,
    artworkUrl = artworkUrl,
    isFavorite = isFavorite
)
