package com.ahmoneam.instabug.codechallenge.di

import android.content.Context
import com.ahmoneam.instabug.core.di.SL

object DI {
    fun init(context: Context) {
        SL.init(context)
    }
}