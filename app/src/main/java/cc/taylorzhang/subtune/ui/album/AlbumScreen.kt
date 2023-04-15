package cc.taylorzhang.subtune.ui.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.model.Album
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

private const val COLUMN_COUNT = 3
private val ITEM_SPACING = 8.dp

@Composable
fun AlbumScreen(viewModel: AlbumViewModel = getViewModel()) {
    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()
    val albums = uiState.albumPagingDataFlow.collectAsLazyPagingItemsProxy()
    var showAlbumSortOrFilterChoiceDialog by remember { mutableStateOf(false) }

    LaunchedEffect(albums.loadState.refresh) {
        if (albums.loadState.refresh is LoadState.NotLoading && albums.itemCount > 0) {
            val id = albums[0]?.id
            if (viewModel.firstItemId != id) {
                viewModel.firstItemId = id
                viewModel.gridState.scrollToItem(0)
            }
        }
    }

    AlbumSortOrFilterChoiceDialog(
        visible = showAlbumSortOrFilterChoiceDialog,
        currentSort = uiState.sortType,
        onClosed = { showAlbumSortOrFilterChoiceDialog = false },
        onChoice = { viewModel.updateSortType(it) },
    )

    AlbumContent(
        albums = albums,
        onSortOrFilterClick = { showAlbumSortOrFilterChoiceDialog = true },
        onSearchClick = { navController.navigate(Screen.Search.route) },
        onRefresh = { albums.refresh() },
        coverArtUrlGetter = { viewModel.getCoverArtUrl(it) },
        onItemClick = { navController.navigate(Screen.AlbumDetail.argsRoute(it.id)) },
        gridState = viewModel.gridState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumContent(
    albums: LazyPagingItemsProxy<Album>,
    onSortOrFilterClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRefresh: () -> Unit,
    coverArtUrlGetter: (Album) -> String,
    onItemClick: (Album) -> Unit,
    gridState: LazyGridState,
) {
    val isLoading = albums.loadState.refresh is LoadState.Loading
    val refreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    Column(
        modifier = Modifier.previewBackground(MaterialTheme.colorScheme.surface),
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
            title = { Text(stringResource(id = R.string.album)) },
            actions = {
                IconButton(onClick = onSortOrFilterClick) {
                    Icon(
                        Icons.Filled.FilterList,
                        stringResource(id = R.string.album_sort_or_filter),
                    )
                }
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
            if (isLoading && albums.itemCount == 0) {
                LoadingLayout()
                return@SwipeRefresh
            } else if (albums.itemCount == 0) {
                if (albums.loadState.refresh is LoadState.NotLoading) {
                    EmptyLayout()
                } else if (albums.loadState.refresh is LoadState.Error) {
                    ErrorLayout(
                        message = (albums.loadState.refresh as LoadState.Error).error.message ?: "",
                        onRetryClick = onRefresh
                    )
                }
                return@SwipeRefresh
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(COLUMN_COUNT),
                modifier = Modifier.fillMaxSize(),
                state = gridState,
                contentPadding = PaddingValues(ITEM_SPACING),
                horizontalArrangement = Arrangement.spacedBy(ITEM_SPACING),
                verticalArrangement = Arrangement.spacedBy(ITEM_SPACING),
            ) {
                items(albums, key = { it.id }) { album ->
                    album?.let {
                        AlbumItem(
                            album = it,
                            coverArtUrl = coverArtUrlGetter(it),
                            onClick = { onItemClick(it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumItem(album: Album, coverArtUrl: String, onClick: () -> Unit) {
    Column {
        AsyncImage(
            model = coverArtUrl,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.cover_default),
            error = painterResource(id = R.drawable.cover_default),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = album.name,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        Text(
            text = album.artist,
            modifier = Modifier.padding(top = 2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
fun AlbumScreenPreview() {
    SubTuneTheme {
        AlbumContent(
            albums = lazyPagingItemsPreview(FakeDataUtil.listAlbums()),
            onSortOrFilterClick = { },
            onSearchClick = { },
            onRefresh = { },
            coverArtUrlGetter = { "" },
            onItemClick = { },
            gridState = rememberLazyGridState(),
        )
    }
}