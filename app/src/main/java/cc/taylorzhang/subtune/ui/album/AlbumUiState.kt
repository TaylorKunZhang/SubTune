package cc.taylorzhang.subtune.ui.album

import androidx.paging.PagingData
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Song
import kotlinx.coroutines.flow.Flow

data class AlbumUiState(
    val sortType: String,
    val albumPagingDataFlow: Flow<PagingData<Album>>,
    val isLoading: Boolean = false,
    val randomSongs: List<Song>? = null,
)