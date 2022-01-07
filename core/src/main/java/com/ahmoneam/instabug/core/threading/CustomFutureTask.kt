package com.ahmoneam.instabug.core.threading

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.FutureTask

class CustomFutureTask<T : Any?>(callable: Callable<T>) : FutureTask<T>(callable) {
    private var onFinishCallback: ((Result<T?>) -> Unit)? = null
    private var onMain = false

    fun onFinish(onMain: Boolean = false, call: (Result<T?>) -> Unit): CustomFutureTask<T> {
        this.onMain = onMain
        onFinishCallback = call
        return this
    }

    override fun done() {
        super.done()
        val result: Result<T> = try {
            if (isCancelled) Result.failure(CancellationException())
            else Result.success(get())
        } catch (t: Throwable) {
            Result.failure(t)
        }
        if (onMain) Handler(Looper.getMainLooper()).post { onFinishCallback?.invoke(result) }
        else onFinishCallback?.invoke(result)
    }
}