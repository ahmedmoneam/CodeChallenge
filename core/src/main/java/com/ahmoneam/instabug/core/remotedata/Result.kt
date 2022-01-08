package com.ahmoneam.instabug.core.remotedata

import com.ahmoneam.instabug.core.error.AppException
import com.ahmoneam.instabug.core.error.ErrorType

sealed class Result<out Data> {
    data class Success<out Data>(val value: Data) : Result<Data>()
    data class Failure(val reason: AppException, val type: ErrorType = reason.type) :
        Result<Nothing>()
}