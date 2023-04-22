package com.artr.tinkoffcup

import android.app.Application
import com.artr.tinkoffcup.utils.ActivityHelper
import com.artr.tinkoffcup.utils.AppContextHolder

class TinkoffApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppContextHolder.context = this
        ActivityHelper.init(this)
    }
}