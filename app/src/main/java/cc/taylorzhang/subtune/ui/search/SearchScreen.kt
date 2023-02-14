package cc.taylorzhang.subtune.ui.search

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Song
import cc.taylorzhang.subtune.player.LocalAudioPlayer
import cc.taylorzhang.subtune.ui.component.EmptyLayout
import cc.taylorzhang.subtune.ui.component.ErrorLayout
import cc.taylorzhang.subtune.ui.component.LoadingLayout
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.*
import cc.taylorzhang.subtune.util.FakeDataUtil
import coil.compose.AsyncImage
import org.koin.androidx.compose.getViewModel
import kotlin.math.min

private const val DEFAULT_DISPLAY_COUNT = 3

@Composable
fun SearchScreen(viewModel: SearchViewModel = getViewModel()) {
    val navController = LocalNavController.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val audioPlayer = LocalAudioPlayer.current

    SearchContent(
        uiState = uiState,
        onQueryChange = {
            viewModel.updateQuery(it)
            viewModel.search()
        },
        onSearch = { viewModel.search() },
        onClearQuery = { viewModel.updateQuery("") },
        onBackClick = { navController.popBackStack() },
        albumCoverArtUrlGetter = { viewModel.getCoverArtUrl(it) },
        songCoverArtUrlGetter = { viewModel.getCoverArtUrl(it) },
        onAlbumItemClick = {
            navController.navigate(Screen.AlbumDetail.argsRoute(it.id))
        },
        onSongItemClick = {
            if (audioPlayer.setPlaybackList(arrayListOf(it))) {
                navController.navigate(Screen.Playback.route)
            }
        },
        onAlbumMoreClick = { viewModel.updateShowAlbumMore(true) },
        onSongMoreClick = { viewModel.updateShowSongMore(true) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit,
    onBackClick: () -> Unit,
    albumCoverArtUrlGetter: (Album) -> String,
    songCoverArtUrlGetter: (Song) -> String,
    onAlbumItemClick: (Album) -> Unit,
    onSongItemClick: (Song) -> Unit,
    onAlbumMoreClick: () -> Unit,
    onSongMoreClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SearchBar(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                onClearQuery = onClearQuery,
                onBackClick = onBackClick,
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .navigationBarsPadding()
                .imePadding()
        ) {
            when {
                uiState.query.isEmpty() -> {
                    // do nothing
                }

                uiState.isLoading -> {
                    LoadingLayout()
                }

                uiState.error != null -> {
                    ErrorLayout(message = uiState.error.message, onRetryClick = onSearch)
                }

                uiState.album.isEmpty() && uiState.song.isEmpty() -> {
                    EmptyLayout()
                }

                else -> {
                    LazyColumn {
                        if (uiState.album.isNotEmpty()) {
                            searchResultList(
                                titleResId = R.string.album,
                                showMore = uiState.showAlbumMore,
                                onMoreClick = onAlbumMoreClick,
                                items = uiState.album,
                                key = { it.id },
                            ) {
                                SearchResultItem(
                                    coverArtUrl = albumCoverArtUrlGetter(it),
                                    title = it.name,
                                    secondaryText = it.artist,
                                    onClick = { onAlbumItemClick(it) },
                                )
                            }
                        }
                        if (uiState.song.isNotEmpty()) {
                            searchResultList(
                                titleResId = R.string.song,
                                showMore = uiState.showSongMore,
                                onMoreClick = onSongMoreClick,
                                items = uiState.song,
                                key = { it.id },
                            ) {
                                SearchResultItem(
                                    coverArtUrl = songCoverArtUrlGetter(it),
                                    title = it.title,
                                    secondaryText = it.artist,
                                    onClick = { onSongItemClick(it) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit,
    onBackClick: () -> Unit,
    colors: TextFieldColors = SearchBarDefaults.inputFieldColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .statusBarsPadding()
            .height(SearchBarDefaults.InputFieldHeight)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        enabled = true,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = query,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                shape = SearchBarDefaults.inputFieldShape,
                colors = colors,
                contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(),
                container = {},
            )
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

private inline fun <T : Any> LazyListScope.searchResultList(
    @StringRes titleResId: Int,
    showMore: Boolean,
    noinline onMoreClick: () -> Unit,
    items: List<T>,
    noinline key: (item: T) -> Any,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    item(key = titleResId) {
        Text(
            text = stringResource(id = titleResId),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
    items(
        count = if (showMore) items.size else min(items.size, DEFAULT_DISPLAY_COUNT),
        key = { key(items[it]) },
    ) {
        itemContent(items[it])
    }

    if (items.size > DEFAULT_DISPLAY_COUNT && !showMore) {
        item(key = "$titleResId more") {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onMoreClick,
            ) {
                Text(
                    text = stringResource(id = R.string.see_more),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    coverArtUrl: String,
    title: String,
    secondaryText: String?,
    onClick: () -> Unit
) {
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
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        supportingText = {
            secondaryText?.let {
                Text(
                    text = it,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchContent(
        uiState = SearchUiState(
            query = "query",
            album = FakeDataUtil.listAlbums(),
            song = FakeDataUtil.listSongs()
        ),
        onQueryChange = { },
        onSearch = { },
        onClearQuery = { },
        onBackClick = {},
        albumCoverArtUrlGetter = { "" },
        songCoverArtUrlGetter = { "" },
        onAlbumItemClick = { },
        onSongItemClick = { },
        onAlbumMoreClick = { },
        onSongMoreClick = { },
    )
}