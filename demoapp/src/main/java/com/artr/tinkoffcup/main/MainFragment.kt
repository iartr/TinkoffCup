package com.artr.tinkoffcup.main

import android.os.Bundle
import android.view.View
import com.artr.tinkoffcup.R
import com.artr.tinkoffcup.mvvm.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BaseFragment(R.layout.fragment_main) {

    override val viewModel: MainViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = MainFragment().apply { arguments = Bundle.EMPTY }
    }
}