package com.artr.tinkoffcup

import com.artr.tinkoffcup.di.appModule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class DITest : KoinTest {
    @Test
    fun verify() {
        appModule.verify()
    }
}