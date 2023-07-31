package cc.taylorzhang.subtune.model

import androidx.annotation.StringRes
import cc.taylorzhang.subtune.R

enum class AppTheme(@StringRes val labelResId: Int, val value: String) {
    SYSTEM(R.string.theme_follow_system, "system"),
    LIGHT(R.string.theme_light_mode, "light"),
    DARK(R.string.theme_dark_mode, "dark"),
    BLACK(R.string.theme_black_mode, "black");

    companion object {
        fun fromValue(value: String) = values().associateBy(AppTheme::value)[value] ?: SYSTEM
    }
}