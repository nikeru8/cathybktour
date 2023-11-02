package com.daniel.cathybktour.repository

import com.daniel.cathybktour.api.TaipeiTourService
import com.daniel.cathybktour.api.TourModel
import retrofit2.Response

class MainActivityRepository(private val taipeiTourService: TaipeiTourService) {

    suspend fun callTaipeiService(language: String, page: Int?): Response<TourModel> {
        return taipeiTourService.getAttractions(language, page)
    }

}