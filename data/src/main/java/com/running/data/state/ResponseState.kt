package com.running.data.state
sealed class ResponseState<T>{
    data class Success<T>(val data: T): ResponseState<T>()
    data class Error<T>(val message: String = ""): ResponseState<T>()
}
