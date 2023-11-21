package com.daniel.cathybktour.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.TourModel
import com.daniel.cathybktour.databinding.ActivitySplashBinding
import com.daniel.cathybktour.utils.AppDatabase
import com.daniel.cathybktour.view.main.MainActivity
import com.daniel.cathybktour.view.main.viewModel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        binding = ActivitySplashBinding.inflate(layoutInflater).apply {

            setContentView(this.root)
            splashViewModel = viewModel

        }

        initData()
        initObserver()

    }

    private fun initData() {

        viewModel.fetchAllPages("zh-tw") // 假設語言是英文

    }

    private fun initObserver() {

        viewModel.apiResults.observe(this) { responses ->

            val appDatabase = AppDatabase.getDatabase(this)

            lifecycleScope.launch(Dispatchers.IO) {
                responses.forEach { result ->
                    val tourModel = result.getOrNull()?.body()
                    val updatedTourModel = updateTourItemIds(tourModel)
                    updatedTourModel?.data?.let { tourItems ->
                        appDatabase.tourItemDao().insertAll(tourItems)
                    }
                }
            }

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()

        }

        //database
        viewModel.allTourItems.observe(this) { model ->

            Log.d("TAG", "checkpoint model size - ${model.size}")

        }

    }

    fun updateTourItemIds(tourModel: TourModel?): TourModel? {
        tourModel?.data?.forEach { tourItem ->

            val tourItemId = tourItem.id ?: return@forEach

            tourItem.category?.forEach { it?.tourItemId = tourItemId }
            tourItem.friendly?.forEach { it?.tourItemId = tourItemId }
            tourItem.images.forEach { it?.tourItemId = tourItemId }
            tourItem.links?.forEach { it?.tourItemId = tourItemId }
            tourItem.service?.forEach { it?.tourItemId = tourItemId }
            tourItem.target?.forEach { it?.tourItemId = tourItemId }
            tourItem.files?.forEach { it?.tourItemId = tourItemId }
        }

        return tourModel
    }

}