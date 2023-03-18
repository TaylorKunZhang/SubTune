package cc.taylorzhang.subtune.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.data.repository.ServerRepository
import cc.taylorzhang.subtune.model.Server
import cc.taylorzhang.subtune.model.onError
import cc.taylorzhang.subtune.model.onSuccess
import cc.taylorzhang.subtune.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class LoginViewModel(
    private val serverRepository: ServerRepository,
) : ViewModel() {

    private val salt = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val server = serverRepository.serverFlow.value
        _uiState.update {
            it.copy(
                url = server.url,
                username = server.username,
                httpsEnabled = server.httpsEnabled,
            )
        }
    }

    fun updateUrl(url: String) {
        _uiState.update { it.copy(url = url.trim()) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username.trim()) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password.trim()) }
    }

    fun togglePasswordVisible() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleHttpsEnabled() {
        _uiState.update { it.copy(httpsEnabled = !it.httpsEnabled) }
    }

    fun toggleForcePlaintextPassword() {
        _uiState.update { it.copy(forcePlaintextPassword = !it.forcePlaintextPassword) }
    }

    fun login() = viewModelScope.launch {
        if (!checkInput()) return@launch
        serverRepository.update(toServer())
        _uiState.update { it.copy(isLoading = true) }
        serverRepository.ping().onSuccess {
            serverRepository.updateLoginState(true)
            _uiState.update { it.copy(isLoading = false, loggedIn = true) }
        }.onError { error ->
            _uiState.update { it.copy(isLoading = false, loggedIn = false) }
            ToastUtil.longToast(error.message)
        }
    }

    private fun checkInput(): Boolean {
        if (uiState.value.url.isEmpty()) {
            ToastUtil.shortToast(R.string.server_url_required)
            return false
        }
        if (uiState.value.username.isEmpty()) {
            ToastUtil.shortToast(R.string.username_required)
            return false
        }
        if (uiState.value.password.isEmpty()) {
            ToastUtil.shortToast(R.string.password_required)
            return false
        }
        return true
    }

    private fun toServer(): Server {
        var url = uiState.value.url
        var httpsEnabled = uiState.value.httpsEnabled
        if (url.startsWith("https://", ignoreCase = true)) {
            url = url.substring("https://".length)
            httpsEnabled = true
        } else if (url.startsWith("http://", ignoreCase = true)) {
            url = url.substring("http://".length)
            httpsEnabled = false
        }

        return Server(
            url = url,
            username = uiState.value.username,
            token = if (uiState.value.forcePlaintextPassword) "" else md5(uiState.value.password + salt),
            salt = if (uiState.value.forcePlaintextPassword) "" else salt,
            httpsEnabled = httpsEnabled,
            forcePlaintextPassword = uiState.value.forcePlaintextPassword,
            password = if (uiState.value.forcePlaintextPassword) uiState.value.password else "",
            loggedIn = false,
        )
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}