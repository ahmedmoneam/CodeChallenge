package com.ahmoneam.instabug.local.di

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.local.ILocalDataSource
import com.ahmoneam.instabug.local.LocalDataSource
import com.ahmoneam.instabug.local.module.word.cache.IWordCacheManager
import com.ahmoneam.instabug.local.module.word.cache.WordCacheManager
import com.ahmoneam.instabug.local.sharedpreferences.ISharedPreferences
import com.ahmoneam.instabug.local.sharedpreferences.SharedPreferences

object LocalDI {
    fun init() {
        initWorkCache(WordCacheManager())
        initLocalDataSource(LocalDataSource())
        initSharedPreferences(SharedPreferences())
    }

    fun initWorkCache(wordCacheManager: IWordCacheManager) {
        SL.bindInstance(IWordCacheManager::class.java, wordCacheManager)
    }

    fun initLocalDataSource(localDataSource: ILocalDataSource) {
        SL.bindInstance(ILocalDataSource::class.java, localDataSource)
    }

    fun initSharedPreferences(sharedPreferences: ISharedPreferences) {
        SL.bindInstance(ISharedPreferences::class.java, sharedPreferences)
    }
}