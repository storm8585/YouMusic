package tm.you.music.feature.library.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import tm.you.music.core.model.Song
import tm.you.music.feature.library.LibraryRoute

const val LIBRARY_ROUTE = "library_route"

fun NavController.navigateToLibrary(navOptions: NavOptions? = null) {
    this.navigate(LIBRARY_ROUTE, navOptions)
}

fun NavGraphBuilder.libraryScreen(
    onSongClick: (Song) -> Unit
) {
    composable(route = LIBRARY_ROUTE) {
        LibraryRoute(onSongClick = onSongClick)
    }
}
