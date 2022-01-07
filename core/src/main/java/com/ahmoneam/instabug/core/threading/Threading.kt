package com.ahmoneam.instabug.core.threading

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Threading {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)

    fun <T> execute(call: () -> T): CustomFutureTask<T> {
        val futureTask = CustomFutureTask { call() }
        executorService.execute(futureTask)
        return futureTask
    }

    fun <T> execute(
        call: () -> T,
        onSuccess: (T) -> Unit,
        onFail: (Throwable) -> Unit,
    ) {
        executorService.execute {
            try {
                val call1 = call()
                onSuccess(call1)
            } catch (t: Throwable) {
                onFail(t)
            }
        }
    }
}

