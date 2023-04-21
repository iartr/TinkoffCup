package com.artr.tinkoffcup.di

import com.artr.tinkoffcup.main.MainViewModel
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Cicerone.create(Router()) }
    single {
        val cicerone: Cicerone<Router> = get()
        cicerone.router
    }
    single {
        val cicerone: Cicerone<Router> = get()
        cicerone.getNavigatorHolder()
    }

    viewModel { MainViewModel() }
}