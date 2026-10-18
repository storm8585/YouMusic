package tm.you.music.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
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
        modifier = modifier
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
