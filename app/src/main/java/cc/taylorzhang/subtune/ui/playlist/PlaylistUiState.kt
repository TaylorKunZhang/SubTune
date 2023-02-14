package cc.taylorzhang.subtune.ui.playlist

import cc.taylorzhang.subtune.model.Error
import cc.taylorzhang.subtune.model.Playlist

data class PlaylistUiState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val playlists: List<Playlist> = emptyList(),
)