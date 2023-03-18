package cc.taylorzhang.subtune.model

data class Server(
    val url: String = "",
    val username: String = "",
    val token: String = "",
    val salt: String = "",
    val httpsEnabled: Boolean = false,
    val forcePlaintextPassword: Boolean = false,
    val password: String = "",
    val loggedIn: Boolean = false,
)