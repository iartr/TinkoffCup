package com.artr.tinkoffcup.utils

import android.annotation.SuppressLint
import android.content.Context

// Global app context holder, so context is not leaking
@SuppressLint("StaticFieldLeak")
object AppContextHolder {
    lateinit var context: Context

    fun isInitialized(): Boolean = AppContextHolder::context.isInitialized
}