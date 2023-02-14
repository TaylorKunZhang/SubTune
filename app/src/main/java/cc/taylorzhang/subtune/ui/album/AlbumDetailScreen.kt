package cc.taylorzhang.subtune.ui.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Song
import cc.taylorzhang.subtune.player.LocalAudioPlayer
import cc.taylorzhang.subtune.ui.component.BottomPlayerBar
import cc.taylorzhang.subtune.ui.component.EmptyLayout
import cc.taylorzhang.subtune.ui.component.ErrorLayout
import cc.taylorzhang.subtune.ui.component.LoadingLayout
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.playback.PlaybackListDialog
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.util.FakeDataUtil
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AlbumDetailScreen(
    id: String,
    viewModel: AlbumDetailViewModel = getViewModel(parameters = { parametersOf(id) })
) {
    val navController = LocalNavController.current
    val audioPlayer = LocalAudioPlayer.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var isShowPlaybackListDialog by remember { mutableStateOf(false) }

    AlbumDetailContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onRefresh = { viewModel.refresh() },
        onItemClick = { album, index ->
            if (audioPlayer.setPlaybackList(album.song, defaultPosition = index)) {
                navController.navigate(Screen.Playback.route)
            }
        },
        onPlaybackListClick = { isShowPlaybackListDialog = true },
    )

    if (isShowPlaybackListDialog) {
        PlaybackListDialog(onClosed = { isShowPlaybackListDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumDetailContent(
    uiState: AlbumDetailUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onItemClick: (Album, Int) -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    val album = uiState.album
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Text(
                        text = uiState.album?.name ?: "",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingLayout(modifier = Modifier.padding(padding))
            return@Scaffold
        } else if (uiState.error != null) {
            ErrorLayout(
                modifier = Modifier.padding(padding),
                message = uiState.error.message,
                onRetryClick = onRefresh,
            )
            return@Scaffold
        } else if (album == null || album.song.isEmpty()) {
            EmptyLayout(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                itemsIndexed(album.song, key = { _, item -> item.id }) { index, item ->
                    AlbumDetailSong(
                        song = item,
                        onClick = { onItemClick(album, index) },
                    )
                }
            }
            BottomPlayerBar(onPlaybackListClick = onPlaybackListClick)
        }
    }
}

@Composable
private fun AlbumDetailSong(song: Song, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineText = {
            Text(
                text = song.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        supportingText = {
            Text(
                text = song.artist,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
    )
}

@Preview
@Composable
private fun AlbumDetailScreenPreview() {
    SubTuneTheme {
        AlbumDetailContent(
            uiState = AlbumDetailUiState(album = FakeDataUtil.getAlbum()),
            onBackClick = { },
            onRefresh = { },
            onItemClick = { _, _ -> },
            onPlaybackListClick = { },
        )
    }
}