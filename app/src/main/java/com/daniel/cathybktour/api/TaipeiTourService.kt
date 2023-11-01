package com.daniel.cathybktour.api

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TaipeiTourService {

    @Headers("accept: application/json")
    @GET("open-api/{language}/Attractions/All")
    fun getAttractions(
        @Path("language") language: String,
        @Query("page") page: Int?,
    ): Call<TourModel>

}