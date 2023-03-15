package cc.taylorzhang.subtune.di

import android.content.Context
import androidx.room.Room
import cc.taylorzhang.subtune.data.database.AppDatabase
import cc.taylorzhang.subtune.data.database.MIGRATION_1_2
import cc.taylorzhang.subtune.data.datastore.ServerPreferences
import cc.taylorzhang.subtune.data.datastore.SettingsPreferences
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.data.repository.ServerRepository
import cc.taylorzhang.subtune.data.repository.SettingsRepository
import org.koin.dsl.module

val dataModule = module {
    single { provideAppDatabase(get()) }

    single { ServerPreferences(get()) }
    single { SettingsPreferences(get()) }

    single { ServerRepository(get(), get()) }
    single { SettingsRepository(get()) }
    single { MusicRepository(get(), get(), get()) }
}

private fun provideAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "subtune")
        .addMigrations(MIGRATION_1_2)
        .build()
}