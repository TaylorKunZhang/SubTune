package cc.taylorzhang.subtune.ui.playlist

import cc.taylorzhang.subtune.model.Error
import cc.taylorzhang.subtune.model.Playlist

data class PlaylistDetailUiState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val playlist: Playlist? = null,
)