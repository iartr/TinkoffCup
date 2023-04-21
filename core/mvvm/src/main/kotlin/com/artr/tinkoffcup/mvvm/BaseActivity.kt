package com.artr.tinkoffcup.mvvm

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.artr.tinkoffcup.utils.ActivityHelper

open class BaseActivity(@LayoutRes layout: Int) : AppCompatActivity(layout) {
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ActivityHelper.onNewIntent(this)
    }

}