package cc.taylorzhang.subtune.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cc.taylorzhang.subtune.model.Server
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "server")

class ServerPreferences(context: Context) {

    companion object {
        private val URL = stringPreferencesKey("url")
        private val USERNAME = stringPreferencesKey("username")
        private val TOKEN = stringPreferencesKey("token")
        private val SALT = stringPreferencesKey("salt")
        private val HTTPS_ENABLED = booleanPreferencesKey("https_enabled")
        private val FORCE_PLAINTEXT_PASSWORD = booleanPreferencesKey("force_plaintext_password")
        private val PASSWORD = stringPreferencesKey("password")
        private val LOGGED_IN = booleanPreferencesKey("logged_in")
    }

    private val dataStore = context.dataStore

    private val _serverFlow = MutableStateFlow(Server())
    val serverFlow = _serverFlow.asStateFlow()

    suspend fun init(): Server {
        return toServer(dataStore.data.first()).apply {
            _serverFlow.value = this
        }
    }

    suspend fun update(server: Server) {
        dataStore.edit { preferences ->
            preferences[URL] = server.url
            preferences[USERNAME] = server.username
            preferences[TOKEN] = server.token
            preferences[SALT] = server.salt
            preferences[HTTPS_ENABLED] = server.httpsEnabled
            preferences[FORCE_PLAINTEXT_PASSWORD] = server.forcePlaintextPassword
            preferences[PASSWORD] = server.password
            preferences[LOGGED_IN] = server.loggedIn
        }
        _serverFlow.value = server
    }

    suspend fun updateLoginState(loggedIn: Boolean) {
        dataStore.edit { preferences -> preferences[LOGGED_IN] = loggedIn }
        _serverFlow.update { it.copy(loggedIn = loggedIn) }
    }

    suspend fun removeAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN)
            preferences.remove(SALT)
            preferences.remove(PASSWORD)
        }
        _serverFlow.update { it.copy(token = "", salt = "", password = "") }
    }

    private fun toServer(preferences: Preferences): Server {
        return Server(
            url = preferences[URL] ?: "",
            username = preferences[USERNAME] ?: "",
            token = preferences[TOKEN] ?: "",
            salt = preferences[SALT] ?: "",
            httpsEnabled = preferences[HTTPS_ENABLED] ?: false,
            forcePlaintextPassword = preferences[FORCE_PLAINTEXT_PASSWORD] ?: false,
            password = preferences[PASSWORD] ?: "",
            loggedIn = preferences[LOGGED_IN] ?: false,
        )
    }
}