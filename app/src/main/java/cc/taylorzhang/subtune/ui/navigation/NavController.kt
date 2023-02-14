package cc.taylorzhang.subtune.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalNavController")
}