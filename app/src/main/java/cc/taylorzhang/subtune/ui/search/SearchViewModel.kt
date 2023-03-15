package cc.taylorzhang.subtune.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.Album
import cc.taylorzhang.subtune.model.Song
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun search() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val query = _uiState.value.query
            if (query.isEmpty()) return@launch
            _uiState.update { it.copy(isLoading = true) }
            musicRepository.search3(query).onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        album = result.album,
                        song = result.song,
                        error = null,
                        showAlbumMore = false,
                        showSongMore = false,
                    )
                }
            }.onError { error ->
                _uiState.update { it.copy(isLoading = false, error = error) }
            }
        }
    }

    fun updateShowAlbumMore(value: Boolean) {
        _uiState.update { it.copy(showAlbumMore = value) }
    }

    fun updateShowSongMore(value: Boolean) {
        _uiState.update { it.copy(showSongMore = value) }
    }

    fun getCoverArtUrl(album: Album): String {
        return musicRepository.getCoverArtUri(album.coverArt ?: "").toString()
    }

    fun getCoverArtUrl(song: Song): String {
        return musicRepository.getCoverArtUri(song.coverArt).toString()
    }
}