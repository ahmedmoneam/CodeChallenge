package com.ahmoneam.instabug.core.error

sealed class ErrorType {
    object Unknown : ErrorType()
    object Cancellation : ErrorType()
}

sealed class NetworkErrorType(val code: Int) : ErrorType() {

    companion object {
        fun getType(code: Int) = when (code) {
            BadRequest.code -> BadRequest
            Unauthorized.code -> Unauthorized
            Forbidden.code -> Forbidden
            NotFound.code -> NotFound
            MethodNotAllowed.code -> MethodNotAllowed
            NotAcceptable.code -> NotAcceptable
            PreconditionFailed.code -> PreconditionFailed
            UnsupportedMediaType.code -> UnsupportedMediaType
            InternalServerError.code -> InternalServerError
            else -> Unexpected
        }
    }

    object Unexpected : NetworkErrorType(-1)
    object NoInternetConnection : NetworkErrorType(-2)

    object BadRequest : NetworkErrorType(400)
    object Unauthorized : NetworkErrorType(401)
    object Forbidden : NetworkErrorType(403)
    object NotFound : NetworkErrorType(404)
    object MethodNotAllowed : NetworkErrorType(405)
    object NotAcceptable : NetworkErrorType(406)
    object PreconditionFailed : NetworkErrorType(412)
    object UnsupportedMediaType : NetworkErrorType(415)
    object InternalServerError : NetworkErrorType(500)
}
