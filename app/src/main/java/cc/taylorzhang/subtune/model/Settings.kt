package cc.taylorzhang.subtune.model

data class Settings(
    val maxBitrateWifi: Int = 0,
    val maxBitrateMobile: Int = 0,
    val albumSortType: String = "",
    val preferredTheme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = false,
)