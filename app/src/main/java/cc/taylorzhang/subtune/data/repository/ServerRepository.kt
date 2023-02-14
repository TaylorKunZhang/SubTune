package cc.taylorzhang.subtune.data.repository

import cc.taylorzhang.subtune.data.datastore.ServerPreferences
import cc.taylorzhang.subtune.data.http.HttpUtil
import cc.taylorzhang.subtune.data.http.SubsonicApi
import cc.taylorzhang.subtune.model.ApiResult
import cc.taylorzhang.subtune.model.Server

class ServerRepository(
    private val subsonicApi: SubsonicApi,
    private val serverPreferences: ServerPreferences,
) {

    val serverFlow = serverPreferences.serverFlow

    suspend fun initServerPreferences(): Server {
        return serverPreferences.init()
    }

    suspend fun ping(): ApiResult<Unit> {
        return HttpUtil.apiCall(handleErrorSelf = true) { subsonicApi.ping().response }
    }

    suspend fun update(server: Server) {
        serverPreferences.update(server)
    }

    suspend fun updateLoginState(loggedIn: Boolean) {
        serverPreferences.updateLoginState(loggedIn)
        if (!loggedIn) {
            serverPreferences.removeTokenAndSalt()
        }
    }
}