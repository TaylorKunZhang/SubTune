package cc.taylorzhang.subtune.ui.playback

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.player.AudioPlayerUiState
import cc.taylorzhang.subtune.player.LocalAudioPlayer
import cc.taylorzhang.subtune.ui.component.CustomAlertDialog
import cc.taylorzhang.subtune.ui.theme.ElevationTokens
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.util.FakeDataUtil
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackListDialog(
    onClosed: (() -> Unit)? = null,
    onCleared: (() -> Unit)? = null,
) {
    val audioPlayer = LocalAudioPlayer.current
    val uiState by audioPlayer.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberSheetState()
    val mediaItems = remember { mutableStateListOf<MediaItem>() }
    val listState = rememberLazyListState()
    var showClearPlaybackListDialog by remember { mutableStateOf(false) }

    BackHandler {
        coroutineScope.launch { sheetState.hide() }
    }

    LaunchedEffect(Unit) {
        mediaItems.addAll(audioPlayer.listMediaItems())
        val index = mediaItems.indexOfFirst { uiState.mediaItem?.mediaId == it.mediaId }
        if (index != -1) {
            listState.scrollToItem(index = index)
        }
    }

    if (sheetState.currentValue != SheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onClosed?.invoke()
            }
        }
    }

    CustomAlertDialog(
        visible = showClearPlaybackListDialog,
        text = stringResource(id = R.string.clear_playback_list_message),
        onClosed = { showClearPlaybackListDialog = false },
        onSure = {
            audioPlayer.clearPlaybackList()
            onCleared?.invoke()
            coroutineScope.launch { sheetState.hide() }
        }
    )

    PlaybackListContent(
        uiState = uiState,
        sheetState = sheetState,
        listState = listState,
        mediaItems = mediaItems,
        onClearClick = { showClearPlaybackListDialog = true },
        onItemClick = {
            audioPlayer.seek(it, 0)
            if (!uiState.playWhenReady) {
                audioPlayer.play()
            }
        },
        onRemoveClick = {
            audioPlayer.removeMediaItem(it)
            mediaItems.removeAt(it)
            if (mediaItems.isEmpty()) {
                onCleared?.invoke()
                coroutineScope.launch { sheetState.hide() }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaybackListContent(
    uiState: AudioPlayerUiState,
    sheetState: SheetState,
    listState: LazyListState,
    mediaItems: List<MediaItem>,
    onClearClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onRemoveClick: (Int) -> Unit,
) {
    val height = LocalConfiguration.current.screenHeightDp.dp * 2 / 5

    ModalBottomSheet(
        onDismissRequest = { },
        sheetState = sheetState,
        tonalElevation = ElevationTokens.Level0,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.current_playback, mediaItems.size),
                    style = MaterialTheme.typography.bodyLarge,
                )
                IconButton(onClick = onClearClick) {
                    Icon(Icons.Filled.DeleteOutline, null)
                }
            }
            LazyColumn(
                state = listState,
            ) {
                itemsIndexed(mediaItems, key = { _, item -> item.mediaId }) { index, item ->
                    PlaybackListItem(
                        mediaMetadata = item.mediaMetadata,
                        isCurrent = uiState.mediaItem?.mediaId == item.mediaId,
                        onClick = { onItemClick(index) },
                        onRemoveClick = { onRemoveClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaybackListItem(
    mediaMetadata: MediaMetadata,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isCurrent) {
                    Icon(Icons.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                AsyncImage(
                    model = mediaMetadata.artworkUri,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.cover_default),
                    error = painterResource(id = R.drawable.cover_default),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        },
        headlineText = {
            Text(
                text = mediaMetadata.title?.toString() ?: "",
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Unspecified,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        supportingText = {
            Text(
                text = mediaMetadata.artist?.toString() ?: "",
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Unspecified,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        trailingContent = {
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Filled.Clear, null)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PlaybackListDialogPreview() {
    val mediaItems = FakeDataUtil.listMediaItems()
    SubTuneTheme {
        PlaybackListContent(
            uiState = AudioPlayerUiState(
                mediaItem = mediaItems[1],
            ),
            sheetState = rememberSheetState(),
            listState = rememberLazyListState(),
            mediaItems = mediaItems,
            onClearClick = { },
            onItemClick = { },
            onRemoveClick = { },
        )
    }
}