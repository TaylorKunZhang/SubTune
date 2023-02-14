package cc.taylorzhang.subtune.ui.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
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
import cc.taylorzhang.subtune.player.PlaybackMode
import cc.taylorzhang.subtune.ui.navigation.LocalNavController
import cc.taylorzhang.subtune.ui.theme.SubTuneTheme
import cc.taylorzhang.subtune.ui.theme.isLight
import cc.taylorzhang.subtune.util.FakeDataUtil
import cc.taylorzhang.subtune.util.FormatUtil
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun PlaybackScreen() {
    val audioPlayer = LocalAudioPlayer.current
    val audioPlayerUiState by audioPlayer.uiState.collectAsStateWithLifecycle()
    val mediaMetadata = audioPlayerUiState.mediaItem?.mediaMetadata ?: MediaMetadata.EMPTY
    val systemUiController = rememberSystemUiController()
    val navController = LocalNavController.current
    var isShowPlaybackListDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        systemUiController.statusBarDarkContentEnabled = false
        onDispose {
            systemUiController.statusBarDarkContentEnabled = MaterialTheme.isLight
        }
    }

    PlaybackContent(
        audioPlayerUiState = audioPlayerUiState,
        mediaMetadata = mediaMetadata,
        onBackClick = { navController.popBackStack() },
        onSeek = { audioPlayer.seek(it) },
        onTogglePlaybackModeClick = { audioPlayer.togglePlaybackMode() },
        onSeekToPreviousClick = { audioPlayer.seekToPrevious() },
        onSeekToNextClick = { audioPlayer.seekToNext() },
        onPauseClick = { audioPlayer.pause() },
        onPlayClick = { audioPlayer.play() },
        onPlaybackListClick = { isShowPlaybackListDialog = true },
    )

    if (isShowPlaybackListDialog) {
        PlaybackListDialog(
            onClosed = { isShowPlaybackListDialog = false },
            onCleared = { navController.popBackStack() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaybackContent(
    audioPlayerUiState: AudioPlayerUiState,
    mediaMetadata: MediaMetadata,
    onBackClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onTogglePlaybackModeClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    onSeekToNextClick: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PlaybackBackground(mediaMetadata)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                PlaybackTopBar(
                    mediaMetadata = mediaMetadata,
                    onBackClick = onBackClick,
                )
            },
            containerColor = Color.Transparent,
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding),
            ) {
                PlaybackCover(mediaMetadata)
                PlaybackDurationProgressBar(audioPlayerUiState, onSeek)
                PlaybackControllerBar(
                    audioPlayerUiState = audioPlayerUiState,
                    onTogglePlaybackModeClick = onTogglePlaybackModeClick,
                    onSeekToPreviousClick = onSeekToPreviousClick,
                    onSeekToNextClick = onSeekToNextClick,
                    onPauseClick = onPauseClick,
                    onPlayClick = onPlayClick,
                    onPlaybackListClick = onPlaybackListClick,
                )
            }
        }
    }
}

@Composable
private fun PlaybackBackground(mediaMetadata: MediaMetadata) {
    AsyncImage(
        model = mediaMetadata.artworkUri,
        contentDescription = null,
        placeholder = ColorPainter(Color.White),
        error = ColorPainter(Color.White),
        modifier = Modifier
            .fillMaxSize()
            .blur(24.dp, 14.dp),
        contentScale = ContentScale.Crop,
    )

    val colorList = arrayListOf(
        Color.Black.copy(alpha = 0.54f),
        Color.Black.copy(alpha = 0.26f),
        Color.Black.copy(alpha = 0.45f),
        Color.Black.copy(alpha = 0.87f),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colorList)),
    )
}

@Composable
private fun ColumnScope.PlaybackCover(mediaMetadata: MediaMetadata) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(15.dp),
        ) {
            AsyncImage(
                model = mediaMetadata.artworkUri,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.cover_default),
                error = painterResource(id = R.drawable.cover_default),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaybackTopBar(mediaMetadata: MediaMetadata, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = mediaMetadata.title?.toString() ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBackIosNew, null, Modifier.rotate(270f), tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun PlaybackDurationProgressBar(
    audioPlayerUiState: AudioPlayerUiState,
    onSeek: (Long) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isInteracting = isPressed || isDragged

    var sliderValueRaw by remember { mutableStateOf(audioPlayerUiState.contentPosition.toFloat()) }
    val sliderValue = if (isInteracting) sliderValueRaw else {
        audioPlayerUiState.contentPosition.toFloat()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = FormatUtil.getStringForTime(sliderValue.toLong()), color = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValueRaw = it },
            onValueChangeFinished = { onSeek(sliderValueRaw.toLong()) },
            modifier = Modifier.weight(1f),
            valueRange = 0f..audioPlayerUiState.contentDuration.toFloat(),
            interactionSource = interactionSource,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f),
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = FormatUtil.getStringForTime(audioPlayerUiState.contentDuration),
            color = Color.White,
        )
    }
}

@Composable
private fun PlaybackControllerBar(
    audioPlayerUiState: AudioPlayerUiState,
    onTogglePlaybackModeClick: () -> Unit,
    onSeekToPreviousClick: () -> Unit,
    onSeekToNextClick: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPlaybackListClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onTogglePlaybackModeClick) {
            val imageVector = when (audioPlayerUiState.playbackMode) {
                PlaybackMode.IN_ORDER -> Icons.Filled.Menu
                PlaybackMode.REPEAT -> Icons.Filled.Repeat
                PlaybackMode.REPEAT_ONE -> Icons.Filled.RepeatOne
                PlaybackMode.SHUFFLE -> Icons.Filled.Shuffle
            }
            Icon(imageVector, null, modifier = Modifier.size(24.dp), tint = Color.White)
        }
        IconButton(onClick = onSeekToPreviousClick) {
            Icon(
                Icons.Filled.SkipPrevious,
                null,
                modifier = Modifier.size(45.dp),
                tint = Color.White
            )
        }
        IconButton(
            onClick = {
                if (audioPlayerUiState.playWhenReady && !audioPlayerUiState.isPlaying) {
                    onPauseClick()
                } else {
                    if (audioPlayerUiState.isPlaying) onPauseClick() else onPlayClick()
                }
            },
        ) {
            if (audioPlayerUiState.playWhenReady && !audioPlayerUiState.isPlaying) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            } else {
                Icon(
                    if (audioPlayerUiState.isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                    null,
                    modifier = Modifier.size(65.dp),
                    tint = Color.White,
                )
            }
        }
        IconButton(onClick = onSeekToNextClick) {
            Icon(Icons.Filled.SkipNext, null, modifier = Modifier.size(45.dp), tint = Color.White)
        }
        IconButton(onClick = onPlaybackListClick) {
            Icon(Icons.Filled.QueueMusic, null, modifier = Modifier.size(24.dp), tint = Color.White)
        }
    }
}

@Preview
@Composable
private fun PlaybackScreenPreview() {
    SubTuneTheme {
        PlaybackContent(
            audioPlayerUiState = AudioPlayerUiState(
                playWhenReady = true,
                isPlaying = true,
                contentDuration = 2 * 60 * 1000,
                contentPosition = 45 * 1000,
                playbackMode = PlaybackMode.REPEAT_ONE,
            ),
            mediaMetadata = FakeDataUtil.getMediaMetadata(),
            onBackClick = { },
            onSeek = { },
            onTogglePlaybackModeClick = { },
            onSeekToPreviousClick = { },
            onSeekToNextClick = { },
            onPauseClick = { },
            onPlayClick = { },
            onPlaybackListClick = { },
        )
    }
}