package com.daniel.cathybktour.repository

import com.daniel.cathybktour.api.RetrofitManager
import com.daniel.cathybktour.api.TourModel
import retrofit2.Response

class MainActivityRepository {

    private val TAG = MainActivityRepository::class.java.simpleName
    private val retrofit = RetrofitManager.callTaipeiTourService("call Tour Data")

    suspend fun callTaipeiService(language: String, page: Int?): Response<TourModel> {

        return retrofit.getAttractions(language, page?:1)

    }

}
