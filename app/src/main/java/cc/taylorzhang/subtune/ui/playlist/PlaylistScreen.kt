package cc.taylorzhang.subtune.ui.playlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.model.Playlist
import cc.taylorzhang.subtune.ui.component.*
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.util.FakeDataUtil
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.getViewModel

@Composable
fun PlaylistScreen(viewModel: PlaylistViewModel = getViewModel()) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.playlists.firstOrNull()?.id) {
        if (uiState.playlists.isNotEmpty()) {
            viewModel.listState.scrollToItem(0)
        }
    }

    PlaylistContent(
        uiState = uiState,
        onSearchClick = { navController.navigate(Screen.Search.route) },
        onRefresh = { viewModel.refresh() },
        coverArtUrlGetter = { viewModel.getCoverArtUrl(it) },
        onItemClick = { navController.navigate(Screen.PlaylistDetail.argsRoute(it.id)) },
        listState = viewModel.listState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistContent(
    uiState: PlaylistUiState,
    onSearchClick: () -> Unit,
    onRefresh: () -> Unit,
    coverArtUrlGetter: (Playlist) -> String,
    onItemClick: (Playlist) -> Unit,
    listState: LazyListState,
) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    Column(
        modifier = Modifier.previewBackground(MaterialTheme.colorScheme.surface),
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
            title = { Text(stringResource(id = R.string.playlist)) },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, stringResource(id = R.string.search))
                }
            },
        )
        SwipeRefresh(
            state = refreshState,
            onRefresh = onRefresh,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            if (uiState.playlists.isEmpty() && uiState.isLoading) {
                LoadingLayout()
                return@SwipeRefresh
            } else if (uiState.playlists.isEmpty()) {
                if (uiState.error == null) {
                    EmptyLayout()
                } else {
                    ErrorLayout(message = uiState.error.message, onRetryClick = onRefresh)
                }
                return@SwipeRefresh
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
            ) {
                items(uiState.playlists, key = { it.id }) {
                    PlaylistItem(
                        playlist = it,
                        coverArtUrl = coverArtUrlGetter(it),
                        onClick = { onItemClick(it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistItem(playlist: Playlist, coverArtUrl: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        leadingContent = {
            AsyncImage(
                model = coverArtUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.cover_default),
                error = painterResource(id = R.drawable.cover_default),
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
        },
        headlineText = {
            Text(
                text = playlist.name,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        },
    )
}

@Preview
@Composable
fun PlaylistScreenPreview() {
    SubTuneTheme {
        PlaylistContent(
            uiState = PlaylistUiState(playlists = FakeDataUtil.listPlaylists()),
            onSearchClick = { },
            onRefresh = { },
            coverArtUrlGetter = { "" },
            onItemClick = { },
            listState = rememberLazyListState(),
        )
    }
}