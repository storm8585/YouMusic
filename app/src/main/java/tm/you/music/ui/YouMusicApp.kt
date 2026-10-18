package tm.you.music.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import tm.you.music.MainViewModel
import tm.you.music.core.designsystem.component.LevitatedMiniPlayer
import tm.you.music.core.designsystem.theme.YouMusicMotion
import tm.you.music.core.model.Song
import tm.you.music.feature.home.navigation.HOME_ROUTE
import tm.you.music.feature.home.navigation.navigateToHome
import tm.you.music.feature.library.navigation.LIBRARY_ROUTE
import tm.you.music.feature.library.navigation.navigateToLibrary
import tm.you.music.feature.player.PlayerRoute
import tm.you.music.feature.search.navigation.SEARCH_ROUTE
import tm.you.music.feature.search.navigation.navigateToSearch
import tm.you.music.navigation.YouMusicBottomBar
import tm.you.music.navigation.YouMusicNavHost
import tm.you.music.navigation.YouMusicTopLevelDestination

@Composable
fun YouMusicApp(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: HOME_ROUTE

    val playbackState by mainViewModel.playbackState.collectAsState()
    var isFullPlayerExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            YouMusicNavHost(
                navController = navController,
                onSongClick = { song ->
                    mainViewModel.playSong(song)
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                LevitatedMiniPlayer(
                    playbackState = playbackState,
                    onExpandPlayer = { isFullPlayerExpanded = true },
                    onTogglePlayPause = mainViewModel::togglePlayPause,
                    onNextTrack = mainViewModel::skipNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )

                YouMusicBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToDestination = { destination ->
                        val topLevelNavOptions = navOptions {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        when (destination) {
                            YouMusicTopLevelDestination.HOME -> navController.navigateToHome(topLevelNavOptions)
                            YouMusicTopLevelDestination.SEARCH -> navController.navigateToSearch(topLevelNavOptions)
                            YouMusicTopLevelDestination.LIBRARY -> navController.navigateToLibrary(topLevelNavOptions)
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            AnimatedVisibility(
                visible = isFullPlayerExpanded,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = YouMusicMotion.OffsetSpatialExpressiveDefault
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = YouMusicMotion.OffsetSpatialExpressiveFast
                )
            ) {
                PlayerRoute(
                    onDismiss = { isFullPlayerExpanded = false },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
