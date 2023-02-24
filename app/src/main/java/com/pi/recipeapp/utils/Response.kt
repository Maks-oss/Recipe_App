package com.pi.recipeapp.utils

sealed class Response<T>(val data: T? = null, val errorMessage: String? = null) {
    class Success<T>(data: T?) : Response<T>(data = data)
    class Error<T>(errorMessage: String?) : Response<T>(errorMessage = errorMessage)
}