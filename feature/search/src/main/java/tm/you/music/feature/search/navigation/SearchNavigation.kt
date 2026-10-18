package tm.you.music.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import tm.you.music.core.model.Song
import tm.you.music.feature.search.SearchRoute

const val SEARCH_ROUTE = "search_route"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(SEARCH_ROUTE, navOptions)
}

fun NavGraphBuilder.searchScreen(
    onSongClick: (Song) -> Unit
) {
    composable(route = SEARCH_ROUTE) {
        SearchRoute(onSongClick = onSongClick)
    }
}
