package cc.taylorzhang.subtune.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaMetadata
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.player.AudioPlayerUiState
import cc.taylorzhang.subtune.player.LocalAudioPlayer
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.navigation.Screen
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.util.FakeDataUtil
import coil.compose.AsyncImage

@Composable
fun BottomPlayerBar(onPlaybackListClick: () -> Unit) {
    if (isPreview()) {
        BottomPlayerBarPreview()
        return
    }
    val navController = LocalNavController.current
    val audioPlayer = LocalAudioPlayer.current
    val uiState by audioPlayer.uiState.collectAsStateWithLifecycle()
    val mediaItem = uiState.mediaItem ?: return
    val mediaMetadata = mediaItem.mediaMetadata

    BottomPlayerBarContent(
        uiState = uiState,
        mediaMetadata = mediaMetadata,
        onClick = { navController.navigate(Screen.Playback.route) },
        onSeekToPreviousClick = { audioPlayer.seekToPrevious() },
        onPlayClick = { audioPlayer.play() },
        onPauseClick = { audioPlayer.pause() },
        onSeekToNextClick = { audioPlayer.seekToNext() },
        onPlaybackListClick = onPlaybackListClick,
    )
}

@Composable
private fun BottomPlayerBarContent(
    uiState: AudioPlayerUiState,
    mediaMetadata: MediaMetadata,
    onClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onSeekToNextClick: () -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        leadingContent = {
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
        },
        headlineText = {
            Text(
                text = mediaMetadata.title?.toString() ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        supportingText = {
            Text(
                text = mediaMetadata.artist?.toString() ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        trailingContent = {
            Row {
                IconButton(onClick = onSeekToPreviousClick) {
                    Icon(
                        Icons.Rounded.SkipPrevious,
                        null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(
                    onClick = {
                        if (uiState.playWhenReady && !uiState.isPlaying) {
                            onPauseClick()
                        } else {
                            if (uiState.isPlaying) onPauseClick() else onPlayClick()
                        }
                    },
                ) {
                    if (uiState.playWhenReady && !uiState.isPlaying) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                IconButton(onClick = onSeekToNextClick) {
                    Icon(
                        Icons.Rounded.SkipNext,
                        null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = onPlaybackListClick) {
                    Icon(
                        Icons.Rounded.QueueMusic,
                        null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun BottomPlayerBarPreview() {
    SubTuneTheme {
        BottomPlayerBarContent(
            uiState = AudioPlayerUiState(),
            mediaMetadata = FakeDataUtil.getMediaMetadata(),
            onClick = { },
            onSeekToPreviousClick = { },
            onPlayClick = { },
            onPauseClick = { },
            onSeekToNextClick = { },
            onPlaybackListClick = { },
        )
    }
}