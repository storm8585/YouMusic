package tm.you.music.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tm.you.music.core.database.YouMusicDatabase
import tm.you.music.core.database.dao.SongDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): YouMusicDatabase = Room.databaseBuilder(
        context,
        YouMusicDatabase::class.java,
        "youmusic-database"
    ).build()

    @Provides
    fun provideSongDao(database: YouMusicDatabase): SongDao = database.songDao()
}
