package com.running.domain.state

/**
 * 2023-11-28
 * pureum
 */
sealed class ResourceState<T>{
    data class Success<T>(val data: T): ResourceState<T>()
    data class Error<T>(val data: T?=null, val message: String): ResourceState<T>()
    class Loading<T>(): ResourceState<T>()
}
