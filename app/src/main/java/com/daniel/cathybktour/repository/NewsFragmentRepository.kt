package com.daniel.cathybktour.repository

import com.daniel.cathybktour.api.TaipeiTourService
import com.daniel.cathybktour.model.NewsModel
import com.daniel.cathybktour.utils.Result

class NewsFragmentRepository(private val taipeiTourService: TaipeiTourService) {

    suspend fun getNews(language: String?, page: Int): Result<NewsModel> {
        return try {

            val response = taipeiTourService.getNews(language, page)
            if (response.isSuccessful && response.body() != null) {

                Result.success(response.body()!!)

            } else {

                // 處理 HTTP 錯誤
                Result.failure(RuntimeException("HTTP error ${response.code()}"))

            }

        } catch (e: Exception) {

            // 處理連線錯誤或其他錯誤
            Result.failure(e)

        }
    }

}
