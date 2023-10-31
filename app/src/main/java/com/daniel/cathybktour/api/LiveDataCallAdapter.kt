package com.daniel.cathybktour.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class LiveDataCallAdapter<R>(responseType: Type) : CallAdapter<R, LiveData<ApiResponse<R>?>?> {
    private val responseType: Type
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>?> {
        return object : LiveData<ApiResponse<R>?>() {
            var started: AtomicBoolean = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(
                            call: Call<R>,
                            response: Response<R>
                        ) {
                            postValue(ApiResponse<R>(response))
                        }

                        override fun onFailure(
                            call: Call<R>,
                            throwable: Throwable
                        ) {
                            postValue(ApiResponse(throwable, call.request().url().toString()))
                        }
                    })
                }
            }
        }
    }

    init {
        this.responseType = responseType
    }
}