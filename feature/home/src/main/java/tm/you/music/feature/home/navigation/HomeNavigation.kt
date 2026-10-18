package tm.you.music.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import tm.you.music.core.model.Song
import tm.you.music.feature.home.HomeRoute

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(HOME_ROUTE, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onSongClick: (Song) -> Unit
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(onSongClick = onSongClick)
    }
}
