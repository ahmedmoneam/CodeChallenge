package com.ahmoneam.instabug.local.module.word.cache

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.local.module.word.entities.WordCache
import com.ahmoneam.instabug.local.sharedpreferences.ISharedPreferences

class WordCacheManager : IWordCacheManager {
    companion object {
        private const val KEY_WORDS = "KEY_WORDS"
    }

    private val sharedPreferences get() = SL[ISharedPreferences::class.java]

    override fun isWordsCacheEmpty() = sharedPreferences.getString(KEY_WORDS).isNullOrEmpty()

    override fun updateWordsCache(list: List<WordCache>) {
        sharedPreferences.putString(
            KEY_WORDS,
            list.joinToString("|") { "${it.text},${it.count}" }
        )
    }

    override fun getWordsCache() =
        sharedPreferences.getString(KEY_WORDS)?.let { s ->
            s.split("|")
                .map { row -> row.split(",").let { WordCache(it[0], it[1].toInt()) } }
        } ?: emptyList()
}