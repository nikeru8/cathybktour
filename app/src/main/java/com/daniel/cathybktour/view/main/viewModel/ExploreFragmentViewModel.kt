package com.daniel.cathybktour.view.main.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.cathybktour.api.TourModel
import com.daniel.cathybktour.model.Language
import com.daniel.cathybktour.repository.ExploreFragmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExploreFragmentViewModel @Inject constructor(var repository: ExploreFragmentRepository) : ViewModel() {

    private val TAG = ExploreFragmentViewModel::class.java.simpleName

    /*
     * true 可以繼續往下讀取
     * false 阻擋
     * */
    private val _isRvLoading = MutableLiveData(true)
    val isRvLoading: LiveData<Boolean> get() = _isRvLoading
    fun setIsRvLoading(value: Boolean) {
        _isRvLoading.value = value
    }

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> get() = _isError

    // 跟踪當前的頁碼
    private val _currentPage = MutableLiveData(1)

    val currentPage: MutableLiveData<Int> get() = _currentPage


    val totalDenominator = MutableLiveData("0")

    val taipeiTourData: MutableLiveData<TourModel?> = MutableLiveData()

    val changeLanguageStatus = MutableLiveData(false)


    private val _selectedTabIndex = MutableLiveData(0)  // 默認選中第一個tab
    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex
    fun switchTab(index: Int) {
        _selectedTabIndex.value = index
    }


    fun callApiTaipeiTour(language: Language?, currentPage: Int?, nlat: Double?, elong: Double?) {

        viewModelScope.launch {
            try {

                val response = repository.callTaipeiService(language?.code.toString(), currentPage, nlat, elong)
                if (response.isSuccessful) {

                    taipeiTourData.postValue(response.body())
                    totalDenominator.postValue(response.body()?.total.toString())
                    _isLoading.postValue(false)

                } else {

                    _isError.postValue(true)

                }

            } catch (e: Exception) {

                Log.d(TAG, "Error: ${e.localizedMessage}")
                _isError.postValue(true)

            }
        }
    }

    fun incrementPage() {

        _isRvLoading.value = false
        val current = _currentPage.value ?: 0
        currentPage.value = current + 1

    }


    //中文 繁體 簡體中間會有 "-" 判斷是否有 "-"
    fun getLocale(language: Language): Locale {

        //中文 繁體 簡體中間會有 "-" 判斷是否有 "-"
        val parts = language.code.split("-")
        return if (parts.size > 1) {
            Locale(parts[0], parts[1].toUpperCase())
        } else {
            Locale(parts[0])
        }

    }
}