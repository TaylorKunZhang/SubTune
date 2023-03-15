package cc.taylorzhang.subtune.ui.album

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import cc.taylorzhang.subtune.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val musicRepository: MusicRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val settingsFlow = settingsRepository.settingsFlow

    private val _uiState = MutableStateFlow(initUiState())
    val uiState = _uiState.asStateFlow()

    val gridState = LazyGridState()

    fun updateSortType(value: String) = viewModelScope.launch {
        settingsRepository.updateAlbumSortType(value)
        musicRepository.clearAlbumCache()
        _uiState.update {
            it.copy(
                sortType = value,
                albumPagingDataFlow = getAlbumPagingDataFlow(value),
            )
        }
    }

    fun getCoverArtUrl(album: Album): String {
        return musicRepository.getCoverArtUri(album.coverArt ?: "").toString()
    }

    private fun initUiState(): AlbumUiState {
        val sortType = settingsFlow.value.albumSortType
        return AlbumUiState(
            sortType = sortType,
            albumPagingDataFlow = getAlbumPagingDataFlow(sortType),
        )
    }

    private fun getAlbumPagingDataFlow(sortType: String): Flow<PagingData<Album>> {
        return musicRepository.fetchAlbumPagingData(sortType).cachedIn(viewModelScope)
    }
}