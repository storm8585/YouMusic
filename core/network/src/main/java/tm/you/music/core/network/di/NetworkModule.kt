package tm.you.music.core.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tm.you.music.core.network.InnertubeMusicDataSource
import tm.you.music.core.network.MusicDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindMusicDataSource(
        dataSource: InnertubeMusicDataSource
    ): MusicDataSource
}
