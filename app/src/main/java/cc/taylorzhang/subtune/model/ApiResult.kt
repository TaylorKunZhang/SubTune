package cc.taylorzhang.subtune.model

sealed interface ApiResult<T : Any>

class ApiSuccess<T : Any>(val data: T) : ApiResult<T>

class ApiError<T : Any>(val error: Error) : ApiResult<T>

inline fun <T : Any> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> = apply {
    if (this is ApiSuccess<T>) {
        block(data)
    }
}

inline fun <T : Any> ApiResult<T>.onError(block: (Error) -> Unit): ApiResult<T> = apply {
    if (this is ApiError<T>) {
        block(error)
    }
}