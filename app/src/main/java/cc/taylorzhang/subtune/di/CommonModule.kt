package cc.taylorzhang.subtune.di

import cc.taylorzhang.subtune.player.AudioPlayer
import com.squareup.moshi.Moshi
import org.koin.dsl.module

val commonModule = module {
    single { provideMoshi() }
    single { AudioPlayer(get(), get()) }
}

private fun provideMoshi(): Moshi {
    return Moshi.Builder()
        .build()
}