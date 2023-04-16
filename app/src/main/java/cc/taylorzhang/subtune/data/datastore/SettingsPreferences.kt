package cc.taylorzhang.subtune.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import cc.taylorzhang.subtune.model.AppTheme
import cc.taylorzhang.subtune.model.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreferences(context: Context) {

    companion object {
        private val MAX_BITRATE_WIFI = intPreferencesKey("max_bitrate_wifi")
        private val MAX_BITRATE_MOBILE = intPreferencesKey("max_bitrate_mobile")
        private val ALBUM_SORT_TYPE = stringPreferencesKey("album_sort_type")
        private val PREFERRED_THEME = stringPreferencesKey("preferred_theme")
        private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val RANDOM_SONG_COUNT = intPreferencesKey("random_song_count")
    }

    private val dataStore = context.dataStore

    private val _settingsFlow = MutableStateFlow(Settings())
    val settingsFlow = _settingsFlow.asStateFlow()

    suspend fun init(): Settings {
        return toSettings(dataStore.data.first()).apply {
            _settingsFlow.value = this
        }
    }

    suspend fun updateMaxBitrateWifi(value: Int) {
        dataStore.edit { preferences -> preferences[MAX_BITRATE_WIFI] = value }
        _settingsFlow.update { it.copy(maxBitrateWifi = value) }
    }

    suspend fun updateMaxBitrateMobile(value: Int) {
        dataStore.edit { preferences -> preferences[MAX_BITRATE_MOBILE] = value }
        _settingsFlow.update { it.copy(maxBitrateMobile = value) }
    }

    suspend fun updateAlbumSortType(value: String) {
        dataStore.edit { preferences -> preferences[ALBUM_SORT_TYPE] = value }
        _settingsFlow.update { it.copy(albumSortType = value) }
    }

    suspend fun updatePreferredTheme(value: AppTheme) {
        dataStore.edit { preferences -> preferences[PREFERRED_THEME] = value.value }
        _settingsFlow.update { it.copy(preferredTheme = value) }
    }

    suspend fun updateDynamicColor(value: Boolean) {
        dataStore.edit { preferences -> preferences[DYNAMIC_COLOR] = value }
        _settingsFlow.update { it.copy(dynamicColor = value) }
    }

    suspend fun updateRandomSongCount(value: Int) {
        dataStore.edit { preferences -> preferences[RANDOM_SONG_COUNT] = value }
        _settingsFlow.update { it.copy(randomSongCount = value) }
    }

    suspend fun clear() {
        val preferences = dataStore.edit { it.clear() }
        _settingsFlow.value = toSettings(preferences)
    }

    private fun toSettings(preferences: Preferences): Settings {
        return Settings(
            maxBitrateWifi = preferences[MAX_BITRATE_WIFI] ?: 0,
            maxBitrateMobile = preferences[MAX_BITRATE_MOBILE] ?: 0,
            albumSortType = preferences[ALBUM_SORT_TYPE] ?: "alphabeticalByArtist",
            preferredTheme = AppTheme.fromValue(preferences[PREFERRED_THEME] ?: ""),
            dynamicColor = preferences[DYNAMIC_COLOR] ?: false,
            randomSongCount = preferences[RANDOM_SONG_COUNT] ?: 100,
        )
    }
}