package com.daniel.cathybktour.repository

import com.daniel.cathybktour.api.TaipeiTourService
import com.daniel.cathybktour.api.TourModel
import retrofit2.Response


class ExploreFragmentRepository(private val taipeiTourService: TaipeiTourService) {

    suspend fun callTaipeiService(language: String, page: Int?, nlat: Double?, elong: Double?): Response<TourModel> {
        return taipeiTourService.getAttractionsNearBy(language, page, nlat, elong)
    }

}