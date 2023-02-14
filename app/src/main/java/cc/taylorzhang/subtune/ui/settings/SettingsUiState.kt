package cc.taylorzhang.subtune.ui.settings

import cc.taylorzhang.subtune.model.Settings

data class SettingsUiState(
    val url: String = "",
    val username: String = "",
    val loggedOut: Boolean = false,
    val settings: Settings = Settings(),
    val isProgress: Boolean = false,
)