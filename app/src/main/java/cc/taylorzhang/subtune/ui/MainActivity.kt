package cc.taylorzhang.subtune.ui

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavHostController
import cc.taylorzhang.subtune.player.AudioPlayer
import cc.taylorzhang.subtune.player.LocalAudioPlayer
import cc.taylorzhang.subtune.player.PlaybackService
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.NavGraph
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.util.NetworkUtil
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val appViewModel by viewModel<AppViewModel>()
    private val audioPlayer: AudioPlayer by inject()

    companion object {
        const val EXTRA_FROM_NOTIFICATION = "extra_from_notification"
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
            val uiState by appViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState) {
                uiState.newIntent?.let {
                    handleNewIntent(it, navController)
                }
                if (uiState.errorLoggedOut) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                    appViewModel.errorLoggedOutHandled()
                }
            }

            SubTuneTheme(
                appTheme = uiState.preferredTheme,
                dynamicColor = uiState.dynamicColor,
            ) {
                CompositionLocalProvider(
                    LocalNavController provides navController,
                    LocalAudioPlayer provides audioPlayer,
                ) {
                    NavGraph()
                }
            }
        }

        initCollectFlow()
        initMediaController()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        appViewModel.updateNewIntent(intent)
    }

    private fun handleNewIntent(intent: Intent, navController: NavHostController) {
        if (intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)) {
            if (navController.currentDestination?.route != Screen.Playback.route) {
                navController.navigate(Screen.Playback.route)
            }
        }
        appViewModel.updateNewIntent(null)
    }

    private fun initCollectFlow() {
        collectLifecycleFlow(NetworkUtil.netTypeFlow) {
            audioPlayer.onNetTypeChanged(it)
        }
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            if (controllerFuture.isDone) {
                audioPlayer.setMediaController(controllerFuture.get())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun <T> LifecycleOwner.collectLifecycleFlow(
        flow: Flow<T>,
        collector: FlowCollector<T>,
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(collector)
            }
        }
    }
}