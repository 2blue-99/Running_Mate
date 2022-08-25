package com.jaehyeon.locationpolylinetest.utils

/**
 * Created by Jaehyeon on 2022/08/16.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}