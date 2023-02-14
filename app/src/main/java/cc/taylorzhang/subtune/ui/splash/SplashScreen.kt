package cc.taylorzhang.subtune.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.ui.theme.isLight
import org.koin.androidx.compose.getViewModel

@Composable
fun SplashScreen(viewModel: SplashViewModel = getViewModel()) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        uiState.loggedIn?.let {
            val route = if (it) Screen.Main.route else Screen.Login.route
            navController.navigate(route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    Box(
        Modifier
            .fillMaxSize()
            .background(if (MaterialTheme.isLight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
    ) {}
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SubTuneTheme {
        SplashContent()
    }
}