package tm.you.music.feature.player.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import tm.you.music.feature.player.PlayerRoute

const val PLAYER_ROUTE = "player_route"

fun NavController.navigateToPlayer(navOptions: NavOptions? = null) {
    this.navigate(PLAYER_ROUTE, navOptions)
}

fun NavGraphBuilder.playerScreen() {
    composable(route = PLAYER_ROUTE) {
        PlayerRoute()
    }
}
