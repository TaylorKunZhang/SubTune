package cc.taylorzhang.subtune.data.repository

import cc.taylorzhang.subtune.data.datastore.SettingsPreferences
import cc.taylorzhang.subtune.model.AppTheme
import cc.taylorzhang.subtune.model.Settings

class SettingsRepository(
    private val settingsPreferences: SettingsPreferences,
) {

    val settingsFlow = settingsPreferences.settingsFlow

    suspend fun initSettingsPreferences(): Settings {
        return settingsPreferences.init()
    }

    suspend fun updateMaxBitrateWifi(value: Int) {
        settingsPreferences.updateMaxBitrateWifi(value)
    }

    suspend fun updateMaxBitrateMobile(value: Int) {
        settingsPreferences.updateMaxBitrateMobile(value)
    }

    suspend fun updateAlbumSortType(value: String) {
        settingsPreferences.updateAlbumSortType(value)
    }

    suspend fun updatePreferredTheme(value: AppTheme) {
        settingsPreferences.updatePreferredTheme(value)
    }

    suspend fun updateDynamicColor(value: Boolean) {
        settingsPreferences.updateDynamicColor(value)
    }

    suspend fun clearCache() {
        settingsPreferences.clear()
    }
}