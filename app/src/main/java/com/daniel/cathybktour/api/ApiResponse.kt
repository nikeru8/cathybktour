package com.daniel.cathybktour.api

import android.util.Log
import androidx.annotation.Nullable
import retrofit2.Response
import java.io.IOException

/**
 * Common class used by API responses.
 * @param <T>
 */
class ApiResponse<T>(
    val code: Int,
    val body: T? = null,
    val errorMessage: String? = null,
    val url: String? = null
) {
    constructor(error: Throwable, mUrl: String) : this(
        code = 500,
        errorMessage = error.message,
        url = mUrl
    )

    constructor(response: Response<T>) : this(
        code = response.code(),
        url = response.raw().request().url().toString(),
        body = if (response.isSuccessful) response.body() else null,
        errorMessage = if (!response.isSuccessful) {
            response.errorBody()?.string() ?: response.message()
        } else null
    )

    val isSuccessful: Boolean
        get() = code in 200..299
}
