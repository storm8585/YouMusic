package tm.you.music.core.common.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: YouMusicDispatchers)

enum class YouMusicDispatchers {
    Default,
    IO,
    Main
}
