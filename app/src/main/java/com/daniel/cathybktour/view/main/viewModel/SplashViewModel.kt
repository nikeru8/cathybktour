package com.daniel.cathybktour.view.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.api.TourModel
import com.daniel.cathybktour.repository.SplashRepository
import com.daniel.cathybktour.utils.TourItemDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel //表示該 ViewModel 要使用 Hilt 來注入其依賴。因為是viewModel生命週期有所不同，需要使用到HitViewModel
class SplashViewModel @Inject constructor(private var repository: SplashRepository, private val tourItemDao: TourItemDao ) : ViewModel() {


    // 修改 LiveData 類型以匹配 fetchAllPages 的返回類型
    private val _apiResults = MutableLiveData<List<Result<Response<TourModel>>>>()
    val apiResults: LiveData<List<Result<Response<TourModel>>>> = _apiResults

    val allTourItems: LiveData<MutableList<TourItem>> = tourItemDao.getAll()

    fun fetchAllPages(language: String) {

        viewModelScope.launch {

            val responses = (1..17).map { page ->
                async {

                    try {
                        Result.success(repository.callTaipeiService(language, page))

                    } catch (e: Exception) {

                        Result.failure(e)

                    }

                }

            }.awaitAll()

            _apiResults.value = responses

        }

    }

}