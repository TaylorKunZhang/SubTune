package cc.taylorzhang.subtune.ui.playlist

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.Playlist
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistUiState())
    val uiState = _uiState.asStateFlow()

    val listState = LazyListState()

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        musicRepository.getPlaylists().onSuccess { playlists ->
            _uiState.update { it.copy(isLoading = false, playlists = playlists, error = null) }
        }.onError { error ->
            _uiState.update { it.copy(isLoading = false, error = error) }
        }
    }

    fun getCoverArtUrl(playlist: Playlist): String {
        return musicRepository.getCoverArtUri(playlist.coverArt).toString()
    }
}