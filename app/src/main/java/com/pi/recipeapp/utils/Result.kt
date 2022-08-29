package com.pi.recipeapp.utils

class Result<T> private constructor(
    val data: T? = null,
    val errorMessage: Int? = null,
    val status: Status = Status.DEFAULT
) {
    companion object {
        fun <T> success(data: T?): Result<T> = Result(data = data, status = Status.SUCCESS)
        fun <T> loading(): Result<T> =
            Result(status = Status.LOADING)

        fun <T> error(errorMessage: Int?): Result<T> =
            Result(errorMessage = errorMessage, status = Status.ERROR)

        fun <T> default(): Result<T> = Result()
    }

}

enum class Status {
    SUCCESS, ERROR, LOADING, DEFAULT
}