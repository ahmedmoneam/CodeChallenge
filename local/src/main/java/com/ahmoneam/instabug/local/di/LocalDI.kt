package com.ahmoneam.instabug.local.di

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.local.ILocalDataSource
import com.ahmoneam.instabug.local.LocalDataSource

object LocalDI {
    fun init() {
        SL.bindInstance(ILocalDataSource::class.java, LocalDataSource())
    }
}