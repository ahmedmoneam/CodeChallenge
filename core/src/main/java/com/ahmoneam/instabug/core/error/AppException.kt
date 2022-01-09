package com.ahmoneam.instabug.core.error

class AppException(
    val type: ErrorType,
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)