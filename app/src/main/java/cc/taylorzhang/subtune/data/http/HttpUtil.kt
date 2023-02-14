package cc.taylorzhang.subtune.data.http

import android.content.Context
import android.net.Uri
import cc.taylorzhang.subtune.R
import cc.taylorzhang.subtune.constant.ErrorCode
import cc.taylorzhang.subtune.data.http.response.BaseSubsonicResponse
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.data.repository.ServerRepository
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import cc.taylorzhang.subtune.model.ApiError
import cc.taylorzhang.subtune.model.ApiResult
import cc.taylorzhang.subtune.model.ApiSuccess
import cc.taylorzhang.subtune.model.Error
import cc.taylorzhang.subtune.util.LogUtil
import cc.taylorzhang.subtune.util.ToastUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.HttpUrl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.UnknownHostException

object HttpUtil : KoinComponent {

    const val DEFAULT_BASE_URL = "http://demo.subsonic.org/"

    /**
     * A unique string identifying the client application.
     */
    const val APPLICATION_IDENTITY = "SubTune"

    /**
     * The protocol version implemented by the client.
     */
    private const val PROTOCOL_VERSION = "1.13.0"

    /**
     * Request data to be returned in this format.
     */
    private const val RESPONSE_DATA_FORMAT = "json"

    private val innerErrorLoggedOutFlow = MutableStateFlow(false)
    val errorLoggedOutFlow = innerErrorLoggedOutFlow.asStateFlow()

    private val context: Context by inject()
    private val serverRepository: ServerRepository by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val musicRepository: MusicRepository by inject()

    suspend inline fun <T : Any> apiCall(
        handleErrorSelf: Boolean = false,
        call: () -> BaseSubsonicResponse<T>
    ): ApiResult<T> {
        return try {
            val response = call()
            val error = response.error
            if (error != null) return handleError(handleErrorSelf, error, null)
            ApiSuccess(response.data!!)
        } catch (e: Throwable) {
            LogUtil.e("apiCall error: $e")
            handleError(handleErrorSelf, null, e)
        }
    }

    fun errorLoggedOutHandled() {
        innerErrorLoggedOutFlow.value = false
    }

    suspend inline fun <T : Any> apiCall(
        handleErrorSelf: Boolean = false,
        call: () -> BaseSubsonicResponse<T>,
        saveCacheBlock: (T) -> Unit,
        readCacheBlock: () -> T?,
    ): ApiResult<T> {
        val result = try {
            val response = call()
            val error = response.error
            if (error != null) return handleError(handleErrorSelf, error, null)

            val data = response.data!!
            saveCacheBlock(data)
            ApiSuccess(data)
        } catch (e: Throwable) {
            LogUtil.e("apiCall error: $e")
            handleError(handleErrorSelf, null, e)
        }
        return if (result is ApiError) {
            readCacheBlock()?.let { ApiSuccess(it) } ?: result
        } else result
    }

    fun baseUrl(): String {
        val server = serverRepository.serverFlow.value
        return StringBuilder()
            .append(if (server.httpsEnabled) "https://" else "http://")
            .append(server.url)
            .toString()
    }

    fun addAuthParameter(builder: Uri.Builder) {
        val server = serverRepository.serverFlow.value
        builder.appendQueryParameter("u", server.username)
            .appendQueryParameter("t", server.token)
            .appendQueryParameter("s", server.salt)
            .appendQueryParameter("v", PROTOCOL_VERSION)
            .appendQueryParameter("c", APPLICATION_IDENTITY)
            .appendQueryParameter("f", RESPONSE_DATA_FORMAT)
    }

    fun addAuthParameter(builder: HttpUrl.Builder) {
        val server = serverRepository.serverFlow.value
        builder.addQueryParameter("u", server.username)
            .addQueryParameter("t", server.token)
            .addQueryParameter("s", server.salt)
            .addQueryParameter("v", PROTOCOL_VERSION)
            .addQueryParameter("c", APPLICATION_IDENTITY)
            .addQueryParameter("f", RESPONSE_DATA_FORMAT)
    }

    suspend fun <T : Any> handleError(
        handleErrorSelf: Boolean,
        error: Error?,
        e: Throwable?
    ): ApiResult<T> {
        val resultError = if (error != null) {
            when (error.code) {
                ErrorCode.GENERIC -> Error(
                    error.code, context.getString(R.string.error_generic)
                )
                ErrorCode.MISSING_PARAMETER -> Error(
                    error.code, context.getString(R.string.error_missing_parameter)
                )
                ErrorCode.UPGRADE_CLIENT -> Error(
                    error.code, context.getString(R.string.error_upgrade_client)
                )
                ErrorCode.UPGRADE_SERVER -> Error(
                    error.code, context.getString(R.string.error_upgrade_server)
                )
                ErrorCode.AUTH_FAIL -> Error(
                    error.code, context.getString(R.string.error_auth_fail)
                )
                ErrorCode.NOT_SUPPORTED_LDAP -> Error(
                    error.code, context.getString(R.string.error_not_supported_ldap)
                )
                ErrorCode.NO_PERMISSION -> Error(
                    error.code, context.getString(R.string.error_no_permission)
                )
                ErrorCode.TRIAL_EXPIRED -> Error(
                    error.code, context.getString(R.string.error_trial_expired)
                )
                ErrorCode.DATA_NOT_FOUND -> Error(
                    error.code, context.getString(R.string.error_data_not_found)
                )
                else -> error
            }
        } else if (e != null) {
            val message = when (e) {
                is UnknownHostException -> context.getString(R.string.error_connect_failed)
                else -> "${e.javaClass.simpleName}: ${e.message}"
            }
            Error(ErrorCode.GENERIC, message)
        } else {
            Error(ErrorCode.GENERIC, context.getString(R.string.error_unknown))
        }

        if (!handleErrorSelf) {
            when (resultError.code) {
                ErrorCode.AUTH_FAIL, ErrorCode.NOT_SUPPORTED_LDAP -> {
                    serverRepository.updateLoginState(false)
                    settingsRepository.clearCache()
                    musicRepository.clearCache()
                    innerErrorLoggedOutFlow.value = true
                    ToastUtil.longToast(resultError.message)
                }
            }
        }

        return ApiError(resultError)
    }
}