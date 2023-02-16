package cc.taylorzhang.subtune.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import cc.taylorzhang.subtune.ui.album.AlbumDetailScreen
import cc.taylorzhang.subtune.ui.login.LoginScreen
import cc.taylorzhang.subtune.ui.main.MainScreen
import cc.taylorzhang.subtune.ui.playback.PlaybackScreen
import cc.taylorzhang.subtune.ui.playlist.PlaylistDetailScreen
import cc.taylorzhang.subtune.ui.search.SearchScreen
import cc.taylorzhang.subtune.ui.settings.AboutScreen
import cc.taylorzhang.subtune.ui.splash.SplashScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

private const val ANIM_DURATION = 300

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph() {
    val navController = LocalNavController.current
    AnimatedNavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen()
        }
        composable(route = Screen.Login.route) {
            LoginScreen()
        }
        composable(route = Screen.Main.route) {
            MainScreen()
        }
        composable(route = Screen.AlbumDetail.route) {
            val id = Screen.AlbumDetail.getArgsId(it.arguments)
            if (id != null) {
                AlbumDetailScreen(id)
            }
        }
        composable(
            route = Screen.Playback.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Up, animationSpec = tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Down, animationSpec = tween(ANIM_DURATION)
                )
            },
        ) {
            PlaybackScreen()
        }
        composable(route = Screen.PlaylistDetail.route) {
            val id = Screen.PlaylistDetail.getArgsId(it.arguments)
            if (id != null) {
                PlaylistDetailScreen(id)
            }
        }
        composable(route = Screen.Search.route) {
            SearchScreen()
        }
        composable(route = Screen.About.route) {
            AboutScreen()
        }
    }
}