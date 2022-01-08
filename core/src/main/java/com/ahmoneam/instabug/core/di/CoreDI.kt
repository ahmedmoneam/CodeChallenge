package com.ahmoneam.instabug.core.di

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object CoreDI {
    fun init() {
        init(Executors.newFixedThreadPool(4))
    }

    fun init(executorService: ExecutorService) {
        SL.bindInstance(ExecutorService::class.java, executorService)
    }
}