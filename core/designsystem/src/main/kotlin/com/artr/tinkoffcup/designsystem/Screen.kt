package com.artr.tinkoffcup.designsystem

import android.content.res.Resources

object Screen {
    fun getDisplayMetrics() = Resources.getSystem().displayMetrics

    fun width() = getDisplayMetrics().widthPixels

    fun height() = getDisplayMetrics().heightPixels
}