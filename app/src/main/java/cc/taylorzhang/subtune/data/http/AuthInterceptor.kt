package cc.taylorzhang.subtune.data.http

import com.google.common.net.HttpHeaders
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val httpUrl = HttpUtil.baseUrl().toHttpUrl()
        val url = request.url.newBuilder().apply {
            scheme(httpUrl.scheme)
            host(httpUrl.host)
            port(httpUrl.port)
            HttpUtil.addAuthParameter(this)
        }.build()
        request = request.newBuilder()
            .url(url)
            .header(HttpHeaders.USER_AGENT, HttpUtil.APPLICATION_IDENTITY)
            .build()
        return chain.proceed(request)
    }
}