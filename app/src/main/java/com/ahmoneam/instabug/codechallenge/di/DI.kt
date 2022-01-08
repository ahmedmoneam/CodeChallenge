package com.ahmoneam.instabug.codechallenge.di

import android.content.Context
import com.ahmoneam.instabug.codechallenge.modules.words.data.IWordsRepository
import com.ahmoneam.instabug.codechallenge.modules.words.data.WordsRepository
import com.ahmoneam.instabug.codechallenge.modules.words.ui.WordsListViewModel
import com.ahmoneam.instabug.codechallenge.modules.words.usecases.GetWordsUseCase
import com.ahmoneam.instabug.core.di.CoreDI
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.local.di.LocalDI
import com.ahmoneam.instabug.remote.di.RemoteDI

object DI {
    fun init(context: Context) {
        SL.init(context)
        CoreDI.init()
        LocalDI.init()
        RemoteDI.init()
        initWordsModule()
    }

    private fun initWordsModule() {
        SL.bindCustomServiceImplementation(
            IWordsRepository::class.java,
            WordsRepository::class.java
        )
        SL.bindCustomServiceImplementation(
            GetWordsUseCase::class.java,
            GetWordsUseCase::class.java
        )
        SL.bindCustomServiceImplementation(
            WordsListViewModel::class.java,
            WordsListViewModel::class.java
        )
    }
}