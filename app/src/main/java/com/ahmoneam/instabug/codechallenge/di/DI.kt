package com.ahmoneam.instabug.codechallenge.di

import android.content.Context
import com.ahmoneam.instabug.core.di.CoreDI
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.remote.di.RemoteDI

object DI {
    fun init(context: Context) {
        SL.init(context)
        CoreDI.init()
        RemoteDI.init()
    }
}