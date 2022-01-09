package com.ahmoneam.instabug.core.utils

import androidx.test.espresso.idling.CountingIdlingResource
import com.ahmoneam.instabug.core.utils.test.IAndroidTestIdlingResource

object IdlingResource : IAndroidTestIdlingResource {
    override val countingIdlingResource = CountingIdlingResource("CodeChallenge", true)

    override fun increment() {
        countingIdlingResource.increment()
    }

    override fun decrement() {
        countingIdlingResource.decrement()
    }
}

