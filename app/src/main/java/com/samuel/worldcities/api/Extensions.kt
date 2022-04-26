package com.samuel.data.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

fun <T : Any?> Flow<Result<T>>.applyCommonSideEffects() =
    onStart { emit(Result.Progress(isLoading = true)) }
        .onCompletion { emit(Result.Progress(isLoading = false)) }