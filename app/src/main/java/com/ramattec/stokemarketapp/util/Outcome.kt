package com.ramattec.stokemarketapp.util

sealed class Outcome<T>(val data: T? = null, val message: String? = null){
    class Success<T>(data: T?): Outcome<T>(data)
    class Failure<T>(message: String, data: T? = null): Outcome<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true): Outcome<T>()
}
