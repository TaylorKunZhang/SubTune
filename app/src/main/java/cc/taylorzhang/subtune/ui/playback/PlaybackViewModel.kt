package cc.taylorzhang.subtune.ui.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.LyricsItem
import cc.taylorzhang.subtune.model.Song
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaybackViewModel(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState = _uiState.asStateFlow()

    fun onSongChanged(song: Song?) = viewModelScope.launch {
        _uiState.update { it.copy(song = song, lyricsList = emptyList()) }
        if (song == null) return@launch

        _uiState.update { it.copy(lyricsLoading = true) }
        musicRepository.getLyrics(artist = song.artist, title = song.title).onSuccess { lyrics ->
            if (song.id == _uiState.value.song?.id) {
                if (lyrics.value.isEmpty()) {
                    _uiState.update { it.copy(lyricsLoading = false, lyricsList = emptyList()) }
                } else {
                    val lyricsList = lyrics.value.split("\n").map { LyricsItem(lyrics = it) }
                        .filter { it.lyrics.isNotEmpty() }
                    _uiState.update { it.copy(lyricsLoading = false, lyricsList = lyricsList) }
                }
            }
        }.onError {
            _uiState.update { it.copy(lyricsLoading = false) }
        }
    }

    fun toggleLyricsMode() {
        _uiState.update { it.copy(showLyrics = !it.showLyrics) }
    }
}