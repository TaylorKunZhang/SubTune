package cc.taylorzhang.subtune.ui.album

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import cc.taylorzhang.subtune.util.ToastUtil
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
    var firstItemId: String? = null

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

    fun randomPlay() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        val count = settingsFlow.value.randomSongCount
        musicRepository.getRandomSongs(count).onSuccess { songs ->
            if (songs.isEmpty()) {
                _uiState.update { it.copy(isLoading = false) }
                ToastUtil.shortToast(R.string.empty_content)
            } else {
                _uiState.update { it.copy(isLoading = false, randomSongs = songs) }
            }
        }.onError { error ->
            _uiState.update { it.copy(isLoading = false) }
            ToastUtil.shortToast(error.message)
        }
    }

    fun randomSongsHandled() {
        _uiState.update { it.copy(randomSongs = null) }
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