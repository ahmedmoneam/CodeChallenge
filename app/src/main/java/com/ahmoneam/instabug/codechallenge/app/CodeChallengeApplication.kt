package com.ahmoneam.instabug.codechallenge.app

import android.app.Application
import com.ahmoneam.instabug.codechallenge.di.DI

class CodeChallengeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DI.init(this)
    }
}

