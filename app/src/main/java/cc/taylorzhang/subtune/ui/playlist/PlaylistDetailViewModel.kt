package cc.taylorzhang.subtune.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.Song
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val id: String,
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        musicRepository.getPlaylist(id).onSuccess { playlist ->
            _uiState.update { it.copy(isLoading = false, playlist = playlist, error = null) }
        }.onError { error ->
            _uiState.update { it.copy(isLoading = false, playlist = null, error = error) }
        }
    }

    fun getSongCoverArtUrl(song: Song): String {
        return musicRepository.getCoverArtUri(song.albumId).toString()
    }
}