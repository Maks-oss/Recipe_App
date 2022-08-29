package com.pi.recipeapp.utils

class Result<T> private constructor(
    val data: T? = null,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
) {
    companion object {
        fun <T> success(data: T?): Result<T> = Result(data = data)
        fun <T> loading(): Result<T> =
            Result(data = null, isLoading = true)
        fun <T> error(errorMessage: Int?): Result<T> =
            Result(data = null, errorMessage = errorMessage)

        fun <T> default(): Result<T> = Result(data = null)
    }
}