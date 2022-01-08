package com.ahmoneam.instabug.core.threading

import android.os.Handler
import android.os.Looper
import com.ahmoneam.instabug.core.di.SL
import java.util.concurrent.ExecutorService

object Threading {
    private val executorService: ExecutorService get() = SL[ExecutorService::class.java]

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

    fun <T, R> CustomFutureTask<T>.map(call: (T) -> R): CustomFutureTask<R> {
        val futureTask = CustomFutureTask { call(this.get()) }
        executorService.execute(futureTask)
        return futureTask
    }

    fun <T> CustomFutureTask<T>.onCompleteOnMain(call: (T?, Throwable?) -> Unit): CustomFutureTask<T> {
        Handler(Looper.getMainLooper()).post { onFinish(call = call) }
        return this
    }
}
