package cc.taylorzhang.subtune.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.data.http.HttpUtil
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                if (_uiState.value.preferredTheme != settings.preferredTheme) {
                    _uiState.update { it.copy(preferredTheme = settings.preferredTheme) }
                }
                if (_uiState.value.dynamicColor != settings.dynamicColor) {
                    _uiState.update { it.copy(dynamicColor = settings.dynamicColor) }
                }
            }
        }
        viewModelScope.launch {
            HttpUtil.errorLoggedOutFlow.collect { errorLoggedOut ->
                _uiState.update { it.copy(errorLoggedOut = errorLoggedOut) }
            }
        }
    }

    fun updateNewIntent(value: Intent?) = viewModelScope.launch {
        _uiState.update { it.copy(newIntent = value) }
    }

    fun errorLoggedOutHandled() {
        HttpUtil.errorLoggedOutHandled()
    }
}