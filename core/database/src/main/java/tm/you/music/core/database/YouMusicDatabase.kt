package tm.you.music.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tm.you.music.core.database.dao.SongDao
import tm.you.music.core.database.model.SongEntity

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class YouMusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}
