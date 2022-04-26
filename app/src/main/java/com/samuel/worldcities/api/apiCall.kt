package com.samuel.data.api

import retrofit2.Response
import java.io.IOException

suspend fun <T : Any?> apiCall(call: suspend () -> Response<T>): Result<T> {
    return try {
        call().run {
            if (isSuccessful) {
                return Result.Success(body())
            }
            return Result.FailureWithResponse(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.Failure(IOException(e))
    }
}