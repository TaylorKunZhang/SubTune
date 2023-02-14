package cc.taylorzhang.subtune.ui.search

import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Error
import cc.taylorzhang.subtune.model.Song

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: Error? = null,
    val album: List<Album> = emptyList(),
    val song: List<Song> = emptyList(),
    val showAlbumMore: Boolean = false,
    val showSongMore: Boolean = false,
)