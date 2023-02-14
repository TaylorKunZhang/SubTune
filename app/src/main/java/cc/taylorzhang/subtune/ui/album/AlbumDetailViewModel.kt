package cc.taylorzhang.subtune.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val id: String,
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        musicRepository.getAlbum(id).onSuccess { album ->
            _uiState.update { it.copy(isLoading = false, album = album, error = null) }
        }.onError { error ->
            _uiState.update { it.copy(isLoading = false, album = null, error = error) }
        }
    }
}