package com.ahmoneam.instabug.local

import com.ahmoneam.instabug.local.module.word.cache.IWordCacheManager

interface ILocalDataSource {
    val wordCacheManager: IWordCacheManager
}