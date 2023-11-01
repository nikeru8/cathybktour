package com.daniel.cathybktour.view.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daniel.cathybktour.api.TourModel
import com.daniel.cathybktour.model.Language
import com.daniel.cathybktour.repository.MainActivityRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivityViewModel() : ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName
    private val repository = MainActivityRepository()

    /*
     * true 可以繼續往下讀取
     * false 阻擋
     * */
    private val _isRvLoading = MutableLiveData<Boolean>(true)
    val isRvLoading: LiveData<Boolean> get() = _isRvLoading
    fun setIsRvLoading(value: Boolean) {
        _isRvLoading.value = value
    }

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val _isError = MutableLiveData<Boolean>(false)
    val isError: LiveData<Boolean> get() = _isError

    // 跟踪當前的頁碼
    private val _currentPage = MutableLiveData<Int>(1)

    val currentPage: MutableLiveData<Int> get() = _currentPage


    val totalDenominator = MutableLiveData<String>("0")

    val checkAdapterSize = MutableLiveData<Unit>()

    //語言
    val languages = listOf(
        Language("繁體中文", "zh-tw", true),//默認被選中
        Language("简体中文", "zh-cn"),
        Language("English", "en"),
        Language("日本語", "ja"),
        Language("한국어", "ko"),
        Language("Español", "es"),
        Language("ภาษาไทย", "th"),
        Language("Tiếng Việt", "vi")
    )

    val taipeiTourData: MutableLiveData<TourModel> = MutableLiveData()

    fun callApiTaipeiTour(language: Language?, currentPage: Int?) {
        return repository.callTaipeiService(language?.code.toString(), currentPage).enqueue(object : Callback<TourModel> {
            @SuppressLint("NullSafeMutableLiveData")
            override fun onResponse(call: Call<TourModel>, response: Response<TourModel>) {

                if (response.isSuccessful) {

                    val model = response.body()
                    taipeiTourData.value = model
                    totalDenominator.value = model?.total.toString()
                    _isLoading.value = false

                    checkAdapterSize.value = Unit //純觸發

                } else {

                    _isError.value = true

                }

            }

            override fun onFailure(call: Call<TourModel>, t: Throwable) {

                _isError.value = true

            }
        })
    }

    fun incrementPage() {
        _isRvLoading.value = false
        val current = _currentPage.value ?: 0
        currentPage.value = current + 1
    }

    fun getSelectedLanguage(): Language? {
        return languages.firstOrNull { it.isSelected }
    }

    //目前選中的語言
    private val _currentLanguage = MutableLiveData<Language>().apply {
        value = languages.first { it.isSelected }
    }

    val currentLanguage: MutableLiveData<Language> get() = _currentLanguage

    fun updateLanguage(language: Language) {

        _isLoading.value = true
        languages.forEach { it.isSelected = false } //選中條件全部改成false
        language.isSelected = true

        _currentPage.value = 1 // 每當語言更改時，將頁碼重置為1
        _currentLanguage.value = language //更改當前語言

    }

}