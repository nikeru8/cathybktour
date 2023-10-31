package com.daniel.cathybktour.api

import android.util.Log
import androidx.annotation.Nullable
import retrofit2.Response
import java.io.IOException

/**
 * Common class used by API responses.
 * @param <T>
 */
class ApiResponse<T> {

    var code: Int = 500

    @Nullable
    val body: T?

    @Nullable
    val errorMessage: String?

    @Nullable
    var url: String?

    constructor(error: Throwable, mUrl: String) {
        code = 500
        body = null
        errorMessage = error.message
        url = mUrl
    }

    constructor(response: Response<T>) {

        code = response.code()
        url = response.raw().request().url().toString()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = null

        } else {

            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()!!.string()
                } catch (ignored: IOException) {
                    Log.e(ignored.toString(), "error while parsing response")
                }
            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null

        }

    }

    val isSuccessful: Boolean
        get() = code in 200..299

    val getStatus: Int = code

}