package cc.taylorzhang.subtune.player

import androidx.media3.common.MediaItem
import cc.taylorzhang.subtune.model.Song

data class AudioPlayerUiState(
    val playWhenReady: Boolean = false,
    val isPlaying: Boolean = false,
    val contentDuration: Long = 0L,
    val contentPosition: Long = 0L,
    val mediaItem: MediaItem? = null,
    val song: Song? = null,
    val playbackMode: PlaybackMode = PlaybackMode.IN_ORDER,
)