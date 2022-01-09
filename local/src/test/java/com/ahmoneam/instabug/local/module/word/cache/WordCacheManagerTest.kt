package com.ahmoneam.instabug.local.module.word.cache

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.local.ILocalDataSource
import com.ahmoneam.instabug.local.LocalDataSource
import com.ahmoneam.instabug.local.di.LocalDI
import com.ahmoneam.instabug.local.module.word.entities.WordCache
import com.ahmoneam.instabug.local.sharedpreferences.ISharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class WordCacheManagerTest {
    private val sharedPreferences = mockk<ISharedPreferences>()
    private val map = mutableMapOf<String, Any?>()
    private val localDataSource by lazy { SL[ILocalDataSource::class.java] }

    @Before
    fun setUp() {
        LocalDI.initSharedPreferences(sharedPreferences)
        LocalDI.initWorkCache(WordCacheManager())
        LocalDI.initLocalDataSource(LocalDataSource())
    }

    @Test
    fun isWordsCacheEmpty() {
        every { sharedPreferences.getString(any()) } returns null
        assertThat(localDataSource.wordCacheManager.isWordsCacheEmpty(), equalTo(true))
    }

    @Test
    fun isWordsCacheNotEmpty() {
        every { sharedPreferences.getString(any()) } returns "test"
        val wordsCacheEmpty = localDataSource.wordCacheManager.isWordsCacheEmpty()
        assertThat(wordsCacheEmpty, equalTo(false))
    }

    @Test
    fun updateWordsCache() {
        val slotKey = slot<String>()
        val slotValue = slot<String>()
        every {
            sharedPreferences.putString(
                capture(slotKey),
                capture(slotValue),
            )
        } answers { map[slotKey.captured] = slotValue.captured }

        localDataSource.wordCacheManager.updateWordsCache(
            listOf(
                WordCache("test1", 1),
                WordCache("test2", 2)
            )
        )

        assertThat(slotValue.captured, equalTo("test1,1|test2,2"))
    }

    @Test
    fun getWordsCache() {
        val slotKey = slot<String>()
        val slotValue = slot<String>()
        every {
            sharedPreferences.putString(
                capture(slotKey),
                capture(slotValue),
            )
        } answers { map[slotKey.captured] = slotValue.captured }
        every {
            sharedPreferences.getString(
                capture(slotKey),
            )
        } answers { map[slotKey.captured].toString() }

        localDataSource.wordCacheManager.updateWordsCache(
            listOf(
                WordCache("test1", 1),
                WordCache("test2", 2)
            )
        )

        val wordsCache = localDataSource.wordCacheManager.getWordsCache()
        assertThat(wordsCache.size, equalTo(2))
        assertThat(wordsCache[0].text, equalTo("test1"))
        assertThat(wordsCache[0].count, equalTo(1))
        assertThat(wordsCache[1].text, equalTo("test2"))
        assertThat(wordsCache[1].count, equalTo(2))
    }
}