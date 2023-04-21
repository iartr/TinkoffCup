package com.artr.tinkoffcup.ext

import android.content.res.Resources

object Screen {
    fun getDisplayMetrics() = Resources.getSystem().displayMetrics

    fun width() = getDisplayMetrics().widthPixels

    fun height() = getDisplayMetrics().heightPixels
}