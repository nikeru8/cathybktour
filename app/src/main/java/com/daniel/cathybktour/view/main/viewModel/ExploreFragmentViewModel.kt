package com.daniel.cathybktour.view.main.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.daniel.cathybktour.api.TourModel
import com.daniel.cathybktour.model.Language
import com.daniel.cathybktour.repository.ExploreFragmentRepository
import com.daniel.cathybktour.utils.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreFragmentViewModel @Inject constructor(
    var repository: ExploreFragmentRepository,
    application: Application,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : AndroidViewModel(application) {


    private val TAG = ExploreFragmentViewModel::class.java.simpleName

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Event<String>>()
    val isError: LiveData<Event<String>> get() = _isError

    val totalDenominator = MutableLiveData("0")

    val taipeiTourData: MutableLiveData<TourModel?> = MutableLiveData()

    fun callApiTaipeiTour(language: Language?, currentPage: Int?, nlat: Double?, elong: Double?) {

        _isLoading.value = true

        viewModelScope.launch {
            try {

                val response = repository.callTaipeiService(language?.code.toString(), currentPage, nlat, elong)
                if (response.isSuccessful) {

                    taipeiTourData.postValue(response.body())
                    totalDenominator.postValue(response.body()?.total.toString())
                    _isLoading.postValue(false)

                } else {

                    _isError.postValue(Event("讀取失敗，請稍後再試 ${response.errorBody().toString()}"))

                }

            } catch (e: Exception) {

                Log.d(TAG, "Error: ${e.localizedMessage}")
                _isError.postValue(Event("讀取失敗，請稍後再試 ${e.localizedMessage}"))

            }
        }
    }

    // 位置數據
    private val _locationData = MutableLiveData<Location>()
    val locationData: LiveData<Location> get() = _locationData

    // 位置數據 error
    private val _locationError = MutableLiveData<Event<String>>()
    val locationError: LiveData<Event<String>> get() = _locationError

    // 獲取設備位置
    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        // 这里包含之前 getDeviceLocation() 里的逻辑
        // 成功获取位置后更新 LiveData
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {

                _locationData.value = task.result

            } else {

                _locationError.postValue(Event("無法獲取位置"))

            }
        }
    }

    // 觀察位置和地圖狀態
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> get() = _location

    private val _mapReady = MutableLiveData<GoogleMap?>()
    val mapReady: LiveData<GoogleMap?> get() = _mapReady


    fun initMap(mapFragment: SupportMapFragment) {
        mapFragment.getMapAsync { googleMap ->
            _mapReady.value = googleMap
        }
    }

}