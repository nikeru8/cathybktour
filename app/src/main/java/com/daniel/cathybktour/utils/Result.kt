package com.daniel.cathybktour.utils

sealed class Result<out T> {

    data class success<out T>(val data: T) : Result<T>()
    data class failure(val exception: Exception) : Result<Nothing>()
    object loading : Result<Nothing>()

}