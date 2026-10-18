package tm.you.music.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import tm.you.music.core.designsystem.theme.YouMusicMotion
import tm.you.music.core.model.Song
import tm.you.music.feature.home.navigation.HOME_ROUTE
import tm.you.music.feature.home.navigation.homeScreen
import tm.you.music.feature.library.navigation.libraryScreen
import tm.you.music.feature.player.navigation.playerScreen
import tm.you.music.feature.search.navigation.searchScreen

@Composable
fun YouMusicNavHost(
    navController: NavHostController,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = YouMusicMotion.EffectExpressive) +
                scaleIn(initialScale = 0.96f, animationSpec = YouMusicMotion.SpatialExpressiveFast)
        },
        exitTransition = {
            fadeOut(animationSpec = YouMusicMotion.EffectExpressive) +
                scaleOut(targetScale = 0.98f, animationSpec = YouMusicMotion.SpatialExpressiveFast)
        },
        popEnterTransition = {
            fadeIn(animationSpec = YouMusicMotion.EffectExpressive) +
                scaleIn(initialScale = 0.98f, animationSpec = YouMusicMotion.SpatialExpressiveFast)
        },
        popExitTransition = {
            fadeOut(animationSpec = YouMusicMotion.EffectExpressive) +
                scaleOut(targetScale = 0.96f, animationSpec = YouMusicMotion.SpatialExpressiveFast)
        }
    ) {
        homeScreen(
            onSongClick = onSongClick
        )
        searchScreen(
            onSongClick = onSongClick
        )
        playerScreen()
        libraryScreen(
            onSongClick = onSongClick
        )
    }
}
