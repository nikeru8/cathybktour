package com.daniel.cathybktour.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.daniel.cathybktour.api.ApiResponse
import com.daniel.cathybktour.api.RetrofitManager
import com.daniel.cathybktour.api.TourModel
import retrofit2.Call

class MainActivityRepository {

    private val TAG = MainActivityRepository::class.java.simpleName
    private val retrofit = RetrofitManager.callTaipeiTourService("call Tour Data")

    fun callTaipeiService(language: String, page: Int?): Call<TourModel> {

        Log.d(TAG, "callTaipeiService")
        return retrofit.getAttractions(language, page)

    }

}