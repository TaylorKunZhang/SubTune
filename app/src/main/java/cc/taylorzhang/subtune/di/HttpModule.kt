package cc.taylorzhang.subtune.di

import cc.taylorzhang.subtune.BuildConfig
import cc.taylorzhang.subtune.data.http.AuthInterceptor
import cc.taylorzhang.subtune.data.http.HttpUtil
import cc.taylorzhang.subtune.data.http.SubsonicApi
import cc.taylorzhang.subtune.util.LogUtil
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * The default timeout is 10s.
 */
private const val DEFAULT_TIME_OUT = 10L

val httpModule = module {
    factory { AuthInterceptor() }
    factory { provideOkHttpClient(get()) }
    single { provideSubsonicApi(get()) }
    single { provideRetrofit(get(), get()) }
}

private fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
    return Retrofit.Builder()
        .baseUrl(HttpUtil.DEFAULT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}

private fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient().newBuilder()
        .apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor {
                    LogUtil.d(it)
                }
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }
        .addInterceptor(authInterceptor)
        .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        .build()
}

private fun provideSubsonicApi(retrofit: Retrofit): SubsonicApi {
    return retrofit.create(SubsonicApi::class.java)
}