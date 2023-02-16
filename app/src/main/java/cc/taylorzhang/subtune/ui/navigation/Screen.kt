package cc.taylorzhang.subtune.ui.navigation

import android.os.Bundle

sealed class Screen(val route: String) {

    object Splash : Screen("splash")

    object Login : Screen("login")

    object Main : Screen("main")

    object AlbumDetail : Screen("album_detail/{id}") {
        fun argsRoute(id: String) = "album_detail/$id"
        fun getArgsId(bundle: Bundle?): String? = bundle?.getString("id")
    }

    object Playback : Screen("playback")

    object PlaylistDetail : Screen("playlist_detail/{id}") {
        fun argsRoute(id: String) = "playlist_detail/$id"
        fun getArgsId(bundle: Bundle?): String? = bundle?.getString("id")
    }

    object Search : Screen("search")

    object About : Screen("about")
}