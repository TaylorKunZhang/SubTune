package cc.taylorzhang.subtune.ui.login

data class LoginUiState(
    val url: String = "",
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val httpsEnabled: Boolean = false,
    val forcePlaintextPassword: Boolean = false,
    val isLoading: Boolean = false,
    val loggedIn: Boolean = false,
)