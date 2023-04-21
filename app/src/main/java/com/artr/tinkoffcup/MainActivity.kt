package com.artr.tinkoffcup

import android.os.Bundle
import com.artr.tinkoffcup.main.MainFragment
import com.artr.tinkoffcup.mvvm.BaseActivity
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity(R.layout.activity_main) {
    private val navigator = AppNavigator(activity = this, containerId = com.artr.tinkoffcup.design.R.id.fragment_container)
    private val navigationHolder: NavigatorHolder by inject()
    private val router: Router by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            router.replaceScreen(FragmentScreen { MainFragment.newInstance() })
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        super.onPause()
    }
}