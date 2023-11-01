package com.daniel.cathybktour.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.ActivityMainBinding
import com.daniel.cathybktour.databinding.DialogLanguageSelectionBinding
import com.daniel.cathybktour.model.Language
import com.daniel.cathybktour.utils.ViewModelFactory
import com.daniel.cathybktour.view.adapter.LanguageAdapter
import com.daniel.cathybktour.view.adapter.TourAdapter
import com.jakewharton.rxbinding2.view.clicks
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var tourAdapter: TourAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(MainActivityViewModel::class.java)
        binding.mainViewModel = viewModel
        setContentView(binding.root)

        initData()
        initView()
        initListener()
        initObserver()

    }

    private fun initData() {

        //init adapter
        tourAdapter = TourAdapter { selectedTourItem ->

            val fragment = TourItemDetailFragment.newInstance(selectedTourItem)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  //進入的動畫
                    R.anim.slide_out_left,  //退出的動畫
                    R.anim.slide_in_left,   //返回back
                    R.anim.slide_out_right  //當前 Fragment 退出的動畫（當按下返回鍵時或調用 popBackStack() 時）
                )
                .replace(R.id.fl_content, fragment)
                .addToBackStack(null)
                .commit()

        }
        binding.recyclerview.let { recyclerview ->

            val llm = LinearLayoutManager(this)
            llm.orientation = LinearLayoutManager.VERTICAL
            recyclerview.layoutManager = llm

            recyclerview.adapter = tourAdapter

            //分批顯示
            recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @SuppressLint("StringFormatMatches")
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (viewModel.totalDenominator.value != "0") {
                        binding.title.text = getString(
                            R.string.attractions_title,
                            maxOf(lastVisibleItem, 0), //防止出現-1的情況
                            viewModel.totalDenominator.value
                        )
                    }

                    if (viewModel.isRvLoading.value == true && dy > 0 && lastVisibleItem == totalItemCount - 1) { //滑動到底部

                        viewModel.incrementPage()
                        viewModel.callApiTaipeiTour(viewModel.getSelectedLanguage(), viewModel.currentPage.value) //底部call api

                    }

                }
            })
        }
        //init call api in currentLanguage observe

    }

    private fun initView() {

        binding.toolbar.ivBack.visibility = View.INVISIBLE
        binding.toolbar.llToolbarFeatures.visibility = View.VISIBLE
        binding.toolbar.tvToolbarTitle.text = getString(R.string.app_title)
        binding.layoutLoading.tvPleaseWait.text = getString(R.string.loading_please_wait)

    }

    @SuppressLint("CheckResult")
    private fun initListener() {

        binding.toolbar.llToolbarFeatures.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {

            showLanguageDialog(this) { selectedLanguage ->

                viewModel.updateLanguage(selectedLanguage)

            }

        }

    }

    private fun initObserver() {

        viewModel.taipeiTourData.observe(this) {

            tourAdapter.updateData(it.data)

        }

        viewModel.isError.observe(this) { isError ->
            binding.layoutLoading.apply {

                mainView.visibility = if (isError) View.VISIBLE else View.GONE
                pbLoadingGray.visibility = if (isError) View.GONE else View.VISIBLE
                clError.visibility = if (isError) View.VISIBLE else View.GONE
                tvError.text = getString(R.string.server_error)

            }
        }

        //observe是否loading頁面
        viewModel.isLoading.observe(this) { isLoading ->

            binding.layoutLoading.let { view ->

                view.mainView.visibility = if (isLoading) View.VISIBLE else View.GONE
                view.cdLoading.visibility = View.VISIBLE
                view.clError.visibility = View.GONE

            }

        }

        //判斷是否到達所有頁面底部
        viewModel.checkAdapterSize.observe(this) {

            viewModel.setIsRvLoading(tourAdapter.getAttractionsSize() != viewModel.totalDenominator.value)
            Log.d("TAG", "check tourAdapter.getAttractionsSize() - ${tourAdapter.getAttractionsSize()}")
            Log.d("TAG", "check viewModel.totalDenominator.value - ${viewModel.totalDenominator.value}")
            Log.d("TAG", "check setIsRvLoading - ${viewModel.isRvLoading.value}")
            tourAdapter.showFooter(tourAdapter.getAttractionsSize() != viewModel.totalDenominator.value)

        }

        //更改語言後判斷
        viewModel.currentLanguage.observe(this) { language ->

            //中文 繁體 簡體中間會有 "-" 判斷是否有 "-"
            val parts = language.code.split("-")
            val locale = if (parts.size > 1) {
                Locale(parts[0], parts[1].toUpperCase())
            } else {
                Locale(parts[0])
            }

            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            // 更新 UI
            initView()
            tourAdapter.removeAll()
            Log.d("TAG", "sssss language - $language , viewModel.currentPage.value - ${viewModel.currentPage.value}")
            viewModel.callApiTaipeiTour(language = language, viewModel.currentPage.value)//init call api && selected language call api &&

        }

    }

    private fun showLanguageDialog(context: Context, selectionCallback: (Language) -> Unit) {

        val dialogBinding = DialogLanguageSelectionBinding.inflate(LayoutInflater.from(context))
        val recyclerView: RecyclerView = dialogBinding.recyclerView

        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.selected_language))
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        val adapter = LanguageAdapter(viewModel.languages) { language ->

            //當一個語言被選中時
            viewModel.languages.forEach { it.isSelected = false }
            language.isSelected = true

            selectionCallback(language)
            dialog.dismiss()

        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        dialog.show()

    }

}