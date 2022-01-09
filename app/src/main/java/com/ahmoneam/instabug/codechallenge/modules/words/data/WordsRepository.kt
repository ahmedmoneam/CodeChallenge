package com.ahmoneam.instabug.codechallenge.modules.words.data

import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.codechallenge.modules.words.mapToCacheWord
import com.ahmoneam.instabug.codechallenge.modules.words.mapToWords
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.NetworkErrorType
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.threading.CustomFutureTask
import com.ahmoneam.instabug.core.threading.Threading.map
import com.ahmoneam.instabug.core.threading.Threading.mapFromResult
import com.ahmoneam.instabug.local.ILocalDataSource
import com.ahmoneam.instabug.remote.IRemoteDataSource
import com.ahmoneam.instabug.remote.parser.IHtmlParser

class WordsRepository(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource
) : IWordsRepository {
    private val htmlParser = SL[IHtmlParser::class.java]

    override fun getWords(): CustomFutureTask<Result<List<Word>>> {
        return fetchWords().map {
            if (it is Result.Failure && it.type == NetworkErrorType.NoInternetConnection) {
                if (localDataSource.wordCacheManager.isWordsCacheEmpty()) it
                else Result.Success(getWordsCached())
            } else it
        }
    }

    private fun fetchWords(): CustomFutureTask<Result<List<Word>>> {
        return remoteDataSource.getHtmlPage("https://instabug.com")
            .mapFromResult { htmlParser.parseBodyToWordsWithCount(it) }
            .mapFromResult { it.mapToWords() }
            .mapFromResult {
                if (!it.isNullOrEmpty()) updateWordsCache(it)
                it
            }
    }

    private fun updateWordsCache(words: List<Word>) {
        localDataSource.wordCacheManager.updateWordsCache(words.mapToCacheWord())
    }

    private fun getWordsCached() = localDataSource.wordCacheManager.getWordsCache().mapToWords()
}
