package cc.taylorzhang.subtune.model

data class Server(
    val url: String = "",
    val username: String = "",
    val token: String = "",
    val salt: String = "",
    val httpsEnabled: Boolean = false,
    val loggedIn: Boolean = false,
)