package cc.taylorzhang.subtune.ui.album

import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Error

data class AlbumDetailUiState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val album: Album? = null,
)