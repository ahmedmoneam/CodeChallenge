package com.ahmoneam.instabug.codechallenge.modules.words.data

import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.codechallenge.modules.words.mapToWords
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.threading.CustomFutureTask
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
        return fetchWords()
    }

    private fun fetchWords(): CustomFutureTask<Result<List<Word>>> {
        return remoteDataSource.getHtmlPage("https://instabug.com")
            .mapFromResult { htmlParser.parseBodyToWordsWithCount(it) }
            .mapFromResult { it.mapToWords() }
    }
}