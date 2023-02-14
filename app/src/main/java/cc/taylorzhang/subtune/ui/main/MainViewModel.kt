package cc.taylorzhang.subtune.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    val tabs = MainTab.values()

    private val _uiState = MutableStateFlow(MainUiState(selectedTab = MainTab.ALBUM))
    val uiState = _uiState.asStateFlow()

    fun updateSelectedTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updatePlaybackVisible(visible: Boolean) {
        _uiState.update { it.copy(playbackListVisible = visible) }
    }
}