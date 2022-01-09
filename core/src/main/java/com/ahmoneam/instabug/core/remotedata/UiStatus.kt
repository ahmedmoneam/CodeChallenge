package com.ahmoneam.instabug.core.remotedata

import com.ahmoneam.instabug.core.error.ErrorType

sealed class UiStatus<out T> {
    object Idle : UiStatus<Nothing>()
    object Loading : UiStatus<Nothing>()
    object Empty : UiStatus<Nothing>()
    data class Success<out T>(val data: T) : UiStatus<T>()
    data class Failure(val type: ErrorType, val errorMessage: String? = null) : UiStatus<Nothing>()
}
