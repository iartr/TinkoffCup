package com.artr.tinkoffcup

import android.app.Application
import com.artr.tinkoffcup.di.appModule
import com.artr.tinkoffcup.utils.ActivityHelper
import com.artr.tinkoffcup.utils.AppContextHolder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TinkoffApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppContextHolder.context = this
        ActivityHelper.init(this)
        initDI()
    }

    private fun initDI() {
        startKoin {
            androidLogger()
            androidContext(this@TinkoffApp)
            modules(appModule)
        }
    }
}