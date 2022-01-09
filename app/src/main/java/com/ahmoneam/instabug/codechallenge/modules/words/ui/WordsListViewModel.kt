package com.ahmoneam.instabug.codechallenge.modules.words.ui

import com.ahmoneam.instabug.codechallenge.modules.words.entities.view.WordItemView
import com.ahmoneam.instabug.codechallenge.modules.words.mapToWordItemView
import com.ahmoneam.instabug.codechallenge.modules.words.usecases.GetWordsUseCase
import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.core.error.ErrorType
import com.ahmoneam.instabug.core.remotedata.Result
import com.ahmoneam.instabug.core.remotedata.UiStatus
import com.ahmoneam.instabug.core.threading.Threading.onCompleteOnMain

class WordsListViewModel {
    private val getWordsUseCase by lazy { SL[GetWordsUseCase::class.java] }
    private var onStatusUpdatedListener = mutableListOf<((UiStatus<List<WordItemView>>) -> Unit)?>()
    private var currentStatus: UiStatus<List<WordItemView>> = UiStatus.Idle
    private val cachedList = mutableListOf<WordItemView>()

    init {
        getWords()
    }

    private fun updateStatus(status: UiStatus<List<WordItemView>>) {
        currentStatus = status
        onStatusUpdatedListener.onEach { it?.invoke(status) }
    }

    fun addOnStatusUpdatedListener(listener: ((UiStatus<List<WordItemView>>) -> Unit)? = null) {
        onStatusUpdatedListener.add(listener)
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
                                updateStatus(UiStatus.Success(list.mapToWordItemView()))
                                cachedList.clear()
                                cachedList.addAll(list.mapToWordItemView())
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
        onStatusUpdatedListener.clear()
    }
}
