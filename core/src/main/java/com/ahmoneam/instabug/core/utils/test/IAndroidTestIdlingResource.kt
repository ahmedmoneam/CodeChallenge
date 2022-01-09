package com.ahmoneam.instabug.core.utils.test

import androidx.test.espresso.idling.CountingIdlingResource

interface IAndroidTestIdlingResource {
    val countingIdlingResource: CountingIdlingResource?
        get() = null

    fun increment()
    fun decrement()
}