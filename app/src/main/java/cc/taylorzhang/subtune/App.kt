package cc.taylorzhang.subtune

import android.app.Application
import cc.taylorzhang.subtune.di.commonModule
import cc.taylorzhang.subtune.di.dataModule
import cc.taylorzhang.subtune.di.httpModule
import cc.taylorzhang.subtune.di.viewModelModule
import cc.taylorzhang.subtune.util.NetworkUtil
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        NetworkUtil.onAppStart(this)
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(commonModule, httpModule, dataModule, viewModelModule)
        }
    }
}