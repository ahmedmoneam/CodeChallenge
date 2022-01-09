package com.ahmoneam.instabug.codechallenge.modules.words.ui

import android.util.Log
import com.ahmoneam.instabug.codechallenge.modules.words.domain.Word
import com.ahmoneam.instabug.codechallenge.modules.words.usecases.GetWordsUseCase
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.ErrorType
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.remotedata.UiStatus
import com.ahmoneam.instabug.core.threading.Threading.onCompleteOnMain

class WordsListViewModel {
    private val getWordsUseCase by lazy { SL[GetWordsUseCase::class.java] }
    private var onStatusUpdatedListener: ((UiStatus<List<Word>>) -> Unit)? = null
    private var currentStatus: UiStatus<List<Word>> = UiStatus.Idle
    private val cachedList = mutableListOf<Word>()

    init {
        getWords()
    }

    private fun updateStatus(status: UiStatus<List<Word>>) {
        currentStatus = status
        onStatusUpdatedListener?.invoke(status)
    }

    fun addOnStatusUpdatedListener(listener: ((UiStatus<List<Word>>) -> Unit)? = null) {
        onStatusUpdatedListener = listener
        updateStatus(currentStatus)
    }

    fun getWords() {
        updateStatus(UiStatus.Loading)
        getWordsUseCase()
            .onCompleteOnMain { result, throwable ->
                result?.let {
                    when (it) {
                        is Result.Success -> {
                            val list = it.value
                            if (list.isNullOrEmpty()) updateStatus(UiStatus.Empty)
                            else {
                                updateStatus(UiStatus.Success(list))
                                cachedList.clear()
                                cachedList.addAll(list)
                            }
                        }
                        is Result.Failure -> updateStatus(UiStatus.Failure(it.type))
                    }
                } ?: throwable?.let {
                    updateStatus(UiStatus.Failure(ErrorType.Unknown, it.message))
                }
                updateStatus(UiStatus.Idle)
            }
    }

    fun onRestoreState() {
        updateStatus(UiStatus.Success(cachedList))
        updateStatus(UiStatus.Idle)
    }

    fun destroy() {
        Log.v("MainActivity", "Destroy")
        // todo
    }
}
