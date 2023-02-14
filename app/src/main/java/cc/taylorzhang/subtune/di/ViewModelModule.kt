package cc.taylorzhang.subtune.di

import cc.taylorzhang.subtune.ui.AppViewModel
import cc.taylorzhang.subtune.ui.album.AlbumDetailViewModel
import cc.taylorzhang.subtune.ui.album.AlbumViewModel
import cc.taylorzhang.subtune.ui.login.LoginViewModel
import cc.taylorzhang.subtune.ui.main.MainViewModel
import cc.taylorzhang.subtune.ui.playlist.PlaylistDetailViewModel
import cc.taylorzhang.subtune.ui.playlist.PlaylistViewModel
import cc.taylorzhang.subtune.ui.search.SearchViewModel
import cc.taylorzhang.subtune.ui.settings.SettingsViewModel
import cc.taylorzhang.subtune.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AppViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { MainViewModel() }
    viewModel { AlbumViewModel(get(), get()) }
    viewModel { params -> AlbumDetailViewModel(params[0], get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { params -> PlaylistDetailViewModel(params[0], get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}