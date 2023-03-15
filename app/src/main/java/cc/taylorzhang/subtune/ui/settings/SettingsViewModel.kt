package cc.taylorzhang.subtune.ui.settings

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.data.http.HttpUtil
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.data.repository.ServerRepository
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import cc.taylorzhang.subtune.model.AppTheme
import cc.taylorzhang.subtune.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val serverRepository: ServerRepository,
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val server = serverRepository.serverFlow.value
        _uiState.update { it.copy(url = HttpUtil.baseUrl(), username = server.username) }

        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun updateMaxBitrateWifi(value: Int) = viewModelScope.launch {
        settingsRepository.updateMaxBitrateWifi(value)
    }

    fun updateMaxBitrateMobile(value: Int) = viewModelScope.launch {
        settingsRepository.updateMaxBitrateMobile(value)
    }

    fun updatePreferredTheme(value: AppTheme) = viewModelScope.launch {
        settingsRepository.updatePreferredTheme(value)
    }

    fun updateDynamicColor(value: Boolean) = viewModelScope.launch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            settingsRepository.updateDynamicColor(value)
        } else {
            ToastUtil.shortToast(R.string.feature_version_12_limit)
        }
    }

    fun logout() = viewModelScope.launch {
        _uiState.update { it.copy(isProgress = true) }
        serverRepository.updateLoginState(false)
        settingsRepository.clearCache()
        musicRepository.clearCache()
        _uiState.update { it.copy(isProgress = false, loggedOut = true) }
    }
}