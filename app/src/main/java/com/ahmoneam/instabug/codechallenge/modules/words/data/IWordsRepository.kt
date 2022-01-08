package com.ahmoneam.instabug.codechallenge.modules.words.data

import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.threading.CustomFutureTask

interface IWordsRepository {
    fun getWords(): CustomFutureTask<Result<List<Word>>>
}