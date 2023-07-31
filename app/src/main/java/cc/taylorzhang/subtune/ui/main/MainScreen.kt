package cc.taylorzhang.subtune.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.ui.album.AlbumScreen
import cc.taylorzhang.subtune.ui.album.AlbumScreenPreview
import cc.taylorzhang.subtune.ui.component.BottomPlayerBar
import cc.taylorzhang.subtune.ui.component.isPreview
import cc.taylorzhang.subtune.ui.playback.PlaybackListDialog
import cc.taylorzhang.subtune.ui.playback.getPlaybackListDialogBackgroundColor
import cc.taylorzhang.subtune.ui.playlist.PlaylistScreen
import cc.taylorzhang.subtune.ui.playlist.PlaylistScreenPreview
import cc.taylorzhang.subtune.ui.settings.SettingsScreen
import cc.taylorzhang.subtune.ui.settings.SettingsScreenPreview
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = getViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackListDialogBackgroundColor = getPlaybackListDialogBackgroundColor()
    val systemUiController = rememberSystemUiController()
    val navigationBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
        NavigationBarDefaults.Elevation
    )

    SideEffect {
        if (uiState.playbackListVisible) {
            systemUiController.setNavigationBarColor(playbackListDialogBackgroundColor)
        } else {
            systemUiController.setNavigationBarColor(navigationBarColor)
        }
    }

    MainContent(
        tabs = viewModel.tabs,
        uiState = uiState,
        onTabClick = { viewModel.updateSelectedTab(it) },
        onPlaybackListClick = { viewModel.updatePlaybackVisible(true) },
    )

    if (uiState.playbackListVisible) {
        PlaybackListDialog(onClosed = { viewModel.updatePlaybackVisible(false) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    tabs: Array<MainTab>,
    uiState: MainUiState,
    onTabClick: (MainTab) -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            BottomBar(
                tabs = tabs,
                uiState = uiState,
                onTabClick = onTabClick,
                onPlaybackListClick = onPlaybackListClick,
            )
        }) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AnimatedVisibility(
                uiState.selectedTab == MainTab.ALBUM,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (isPreview()) {
                    AlbumScreenPreview()
                } else {
                    AlbumScreen()
                }
            }
            AnimatedVisibility(
                uiState.selectedTab == MainTab.PLAYLIST,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (isPreview()) {
                    PlaylistScreenPreview()
                } else {
                    PlaylistScreen()
                }
            }
            AnimatedVisibility(
                uiState.selectedTab == MainTab.SETTINGS,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (isPreview()) {
                    SettingsScreenPreview()
                } else {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    tabs: Array<MainTab>,
    uiState: MainUiState,
    onTabClick: (MainTab) -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    Column {
        BottomPlayerBar(onPlaybackListClick = onPlaybackListClick)
        NavigationBar {
            tabs.forEach { tab ->
                val selected = uiState.selectedTab == tab
                val painter = rememberVectorPainter(tab.iconImageVector)
                val selectedPainter = rememberVectorPainter(tab.selectedImageVector)

                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabClick(tab) },
                    icon = {
                        Icon(
                            painter = if (selected) selectedPainter else painter,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    SubTuneTheme {
        MainContent(
            tabs = MainTab.values(),
            uiState = MainUiState(selectedTab = MainTab.ALBUM),
            onTabClick = {},
            onPlaybackListClick = { },
        )
    }
}