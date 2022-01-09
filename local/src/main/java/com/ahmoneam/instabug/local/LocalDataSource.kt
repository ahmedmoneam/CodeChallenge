package com.ahmoneam.instabug.local

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.local.module.word.cache.IWordCacheManager

class LocalDataSource : ILocalDataSource {
    override val wordCacheManager: IWordCacheManager
        get() = SL[IWordCacheManager::class.java]
}