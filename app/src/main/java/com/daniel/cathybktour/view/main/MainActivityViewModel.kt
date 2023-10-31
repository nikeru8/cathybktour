package com.daniel.cathybktour.view.main

import androidx.lifecycle.*
import com.daniel.cathybktour.api.ApiResponse
import com.daniel.cathybktour.api.TourModel
import com.daniel.cathybktour.repository.MainActivityRepository


class MainActivityViewModel() : ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName
    private val repository = MainActivityRepository()


    val isLoading = MutableLiveData<Boolean>().apply {
        value = true //預設 代表loading中
    }

    val isError = MutableLiveData<Boolean>().apply {
        value = false //預設 代表沒有錯誤
    }

    // 使用 MutableLiveData 來跟踪當前的頁碼
    private val _currentPage = MutableLiveData<Int>()

    private val _currentLanguage = MutableLiveData<String>().apply {

        value = "zh-tw" //設定預設語言

    }

    private val _requestParams = MediatorLiveData<Pair<String, Int>>()

    init {

        //新增page頁數
        _requestParams.addSource(_currentPage) { page ->
            _requestParams.value = Pair(_currentLanguage.value ?: "zh-tw", page)
        }
        //新增語言
        _requestParams.addSource(_currentLanguage) { lang ->
            _requestParams.value = Pair(lang, _currentPage.value ?: 1)
        }

    }

    // 使用 LiveData.switchMap 來變換 LiveData
    val taipeiTourData: LiveData<ApiResponse<TourModel>> = _requestParams.switchMap { params ->
        repository.callTaipeiService(params.first, params.second)
    }

    fun setLanguage(lang: String) {

        _currentLanguage.value = lang
        _currentPage.value = 1 // 每當語言更改時，將頁碼重置為1

    }

    fun setIndex(index: Int) {

        _currentPage.value = index

    }


    fun incrementPage() {

        val current = _currentPage.value ?: 0
        _currentPage.value = current + 1

    }

}