package com.samuel.data.api

import retrofit2.Response

sealed class Result<out T : Any?> {

    data class Success<out T : Any>(val result: T?) : Result<T>()
    data class Failure(val exception: Throwable?) : Result<Nothing>()
    data class FailureWithResponse<out T : Any?>(
        val response: Response<*>? = null,
        val exception: Throwable? = null
    ) : Result<T>()

    data class Progress(val isLoading: Boolean) : Result<Nothing>()

    fun getResultOrNull(): T? {
        return when (this) {
            is Success -> this.result
            else -> null
        }
    }

    fun getErrorOrNull(): Throwable? {
        return when (this) {
            is Failure -> this.exception
            else -> null
        }
    }

    fun getResponseOrNull(): Response<*>? {
        return when (this) {
            is FailureWithResponse -> this.response
            else -> null
        }
    }

    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
    fun isFailureWithResponse(): Boolean = this is FailureWithResponse
    fun isProgress(): Boolean = this is Progress

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$result]"
            is Failure -> "Failure[exception=$exception]"
            is FailureWithResponse<Any?> -> "FailureWithResponse[response=$response]"
            is Progress -> "In Progress = $isLoading"
        }
    }
}