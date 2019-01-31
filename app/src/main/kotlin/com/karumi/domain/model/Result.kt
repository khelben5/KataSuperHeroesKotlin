package com.karumi.domain.model

sealed class Result<I>
data class Success<T>(val value: T) : Result<T>()
class NotFound<T> : Result<T>()
class NetworkError<T> : Result<T>()
