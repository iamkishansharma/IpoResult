package com.heycode.iporesult.utils

sealed class Resources<T>(val data: T?, val message: String?) {
    class Success<T>(data: T, message: String) : Resources<T>(data, message) // message for success
    class Error<T>(message: String) : Resources<T>(null, message)
}
