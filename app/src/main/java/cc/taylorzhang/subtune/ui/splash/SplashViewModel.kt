package cc.taylorzhang.subtune.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.repository.ServerRepository
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val serverRepository: ServerRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.initSettingsPreferences()
            val server = serverRepository.initServerPreferences()
            _uiState.update { it.copy(loggedIn = server.loggedIn) }
        }
    }
}