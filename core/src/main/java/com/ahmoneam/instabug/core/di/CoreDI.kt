package com.ahmoneam.instabug.core.di

import com.ahmoneam.instabug.core.utils.test.EmptyIdlingResource
import com.ahmoneam.instabug.core.utils.test.IAndroidTestIdlingResource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object CoreDI {
    fun init() {
        init(Executors.newFixedThreadPool(4))
        initIdlingResource()
    }

    fun initIdlingResource() {
        SL.bindInstance(IAndroidTestIdlingResource::class.java, EmptyIdlingResource)
    }

    fun init(executorService: ExecutorService) {
        SL.bindInstance(ExecutorService::class.java, executorService)
    }
}