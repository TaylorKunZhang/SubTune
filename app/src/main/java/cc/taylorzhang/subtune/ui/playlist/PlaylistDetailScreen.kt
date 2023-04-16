package cc.taylorzhang.subtune.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.model.Playlist
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
import coil.compose.AsyncImage
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlaylistDetailScreen(
    id: String,
    viewModel: PlaylistDetailViewModel = getViewModel(parameters = { parametersOf(id) })
) {
    val navController = LocalNavController.current
    val audioPlayer = LocalAudioPlayer.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isShowPlaybackListDialog by remember { mutableStateOf(false) }

    PlaylistDetailContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onRefresh = { viewModel.refresh() },
        coverArtUrlGetter = { viewModel.getSongCoverArtUrl(it) },
        onItemClick = { playlist, index ->
            if (audioPlayer.setPlaybackList(playlist.entry, defaultPosition = index)) {
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
private fun PlaylistDetailContent(
    uiState: PlaylistDetailUiState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    coverArtUrlGetter: (Song) -> String,
    onItemClick: (Playlist, Int) -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Text(
                        text = uiState.playlist?.name ?: "",
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
        val playlist = uiState.playlist
        if (uiState.isLoading) {
            LoadingLayout(modifier = Modifier.padding(padding))
            return@Scaffold
        } else if (uiState.error != null) {
            ErrorLayout(message = uiState.error.message, onRetryClick = onRefresh)
            return@Scaffold
        } else if (playlist == null || playlist.entry.isEmpty()) {
            EmptyLayout()
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                itemsIndexed(playlist.entry, key = { _, item -> item.id }) { index, item ->
                    PlaylistDetailSong(
                        song = item,
                        coverArtUrl = coverArtUrlGetter(item),
                        onClick = { onItemClick(playlist, index) },
                    )
                }
            }
            BottomPlayerBar(onPlaybackListClick = onPlaybackListClick)
        }
    }
}

@Composable
private fun PlaylistDetailSong(song: Song, coverArtUrl: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        leadingContent = {
            AsyncImage(
                model = coverArtUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_cover_default),
                error = painterResource(id = R.drawable.ic_cover_default),
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
        },
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
        }
    )
}

@Preview
@Composable
private fun PlaylistDetailScreenPreview() {
    SubTuneTheme {
        PlaylistDetailContent(
            uiState = PlaylistDetailUiState(playlist = FakeDataUtil.getPlaylist()),
            onBackClick = { },
            onRefresh = { },
            coverArtUrlGetter = { "" },
            onItemClick = { _, _ -> },
            onPlaybackListClick = { },
        )
    }
}