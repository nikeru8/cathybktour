package com.daniel.cathybktour.view.main.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.cathybktour.model.NewsModel
import com.daniel.cathybktour.repository.NewsFragmentRepository
import com.daniel.cathybktour.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsFragmentRepository) : ViewModel() {

    private val TAG = NewsViewModel::class.java.simpleName

    val taipeiNews: MutableLiveData<Result<NewsModel?>> = MutableLiveData()


    fun fetchNews(language: String?, page: Int) {
        viewModelScope.launch {

            taipeiNews.value = Result.loading
            taipeiNews.value = try {

                repository.getNews(language, page)

            } catch (e: Exception) {

                Result.failure(e)

            }

        }
    }

}