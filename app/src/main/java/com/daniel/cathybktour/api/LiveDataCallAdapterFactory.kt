package com.daniel.cathybktour.api

import androidx.lifecycle.LiveData
import com.daniel.cathybktour.R
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation?>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType: Type = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        require(rawObservableType == ApiResponse::class.java) { "type must be a resource" }
        require(observableType is ParameterizedType) { "resource must be parameterized" }
        val bodyType: Type = getParameterUpperBound(0, observableType as ParameterizedType)
        return LiveDataCallAdapter<R>(bodyType)
    }
}