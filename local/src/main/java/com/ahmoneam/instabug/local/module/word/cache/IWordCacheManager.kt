package com.ahmoneam.instabug.local.module.word.cache

import com.ahmoneam.instabug.local.module.word.entities.WordCache

interface IWordCacheManager {
    fun isWordsCacheEmpty(): Boolean
    fun updateWordsCache(list: List<WordCache>)
    fun getWordsCache(): List<WordCache>
}
