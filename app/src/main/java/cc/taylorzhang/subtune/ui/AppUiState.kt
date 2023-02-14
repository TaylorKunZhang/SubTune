package cc.taylorzhang.subtune.ui

import android.content.Intent
import cc.taylorzhang.subtune.model.AppTheme

data class AppUiState(
    val preferredTheme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = false,
    val newIntent: Intent? = null,
    val errorLoggedOut: Boolean = false,
)