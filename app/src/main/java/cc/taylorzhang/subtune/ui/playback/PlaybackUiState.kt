package cc.taylorzhang.subtune.ui.playback

import cc.taylorzhang.subtune.model.LyricsItem
import cc.taylorzhang.subtune.model.Song

data class PlaybackUiState(
    val song: Song? = null,
    val showLyrics: Boolean = false,
    val lyricsLoading: Boolean = false,
    val lyricsList: List<LyricsItem> = emptyList(),
)