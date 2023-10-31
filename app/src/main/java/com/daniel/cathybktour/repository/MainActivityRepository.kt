package com.daniel.cathybktour.repository

import androidx.lifecycle.LiveData
import com.daniel.cathybktour.api.ApiResponse
import com.daniel.cathybktour.api.RetrofitManager
import com.daniel.cathybktour.api.TourModel

class MainActivityRepository {

    private val TAG = MainActivityRepository::class.java.simpleName
    private val retrofit = RetrofitManager.callTaipeiTourService("call Tour Data")

    fun callTaipeiService(language: String, page: Int): LiveData<ApiResponse<TourModel>> {

        return retrofit.getAttractions(language, page)

    }

}