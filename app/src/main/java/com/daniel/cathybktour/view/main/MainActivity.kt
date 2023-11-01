package com.daniel.cathybktour.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
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
    val llm = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(MainActivityViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {

            setContentView(this.root)
            mainViewModel = viewModel

        }

        initData()
        initView()
        initListener()
        initObserver()

    }

    private fun initData() {

        //init adapter
        tourAdapter = TourAdapter(this@MainActivity, itemClick = { selectedTourItem ->

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

        }) { //更新完currentList後，做判斷

            tourAdapter.showFooter(tourAdapter.getAttractionsSize() != viewModel.totalDenominator.value)
            viewModel.setIsRvLoading(tourAdapter.getAttractionsSize() != viewModel.totalDenominator.value)

        }

        binding.recyclerview.apply {

            llm.orientation = LinearLayoutManager.VERTICAL
            layoutManager = llm

            adapter = tourAdapter

            //分批顯示
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun initView() = binding.run {

        with(toolbar) {
            ivBack.visibility = View.INVISIBLE
            llToolbarFeatures.visibility = View.VISIBLE
            tvToolbarTitle.text = getString(R.string.app_title)
        }

        layoutLoading.tvPleaseWait.text = getString(R.string.loading_please_wait)

        if (viewModel.totalDenominator.value != "0") {

            title.text = getString(R.string.attractions_title, "1", viewModel.totalDenominator.value)

        }

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
        //獲取主要資料
        viewModel.taipeiTourData.observe(this) { tourModel ->

            if (viewModel.changeLanguageStatus.value == true) {

                tourAdapter.submitList(tourModel?.data) {

                    llm.scrollToPosition(0)

                }

                viewModel.changeLanguageStatus.value = false

            } else {

                tourAdapter.updateData(tourModel?.data)

            }

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

            binding.layoutLoading.apply {

                mainView.visibility = if (isLoading) View.VISIBLE else View.GONE
                cdLoading.visibility = View.VISIBLE
                clError.visibility = View.GONE

            }

        }

        //更改語言後判斷
        viewModel.currentLanguage.observe(this) { language ->

            viewModel.changeLanguageStatus.value = true

            val config = Configuration()
            config.setLocale(viewModel.getLocale(language))
            resources.updateConfiguration(config, resources.displayMetrics)

            // 更新 UI
            initView()
            viewModel.callApiTaipeiTour(language = language, viewModel.currentPage.value)//init call api && selected language call api

        }

    }

    //選擇語言dialog
    private fun showLanguageDialog(context: Context, selectionCallback: (Language) -> Unit) {

        DialogLanguageSelectionBinding.inflate(LayoutInflater.from(context)).apply {

            val builder = AlertDialog.Builder(context)
                .setTitle(getString(R.string.selected_language))
                .setView(this.root)
                .create()

            recyclerView.run {

                layoutManager = LinearLayoutManager(context)
                adapter = LanguageAdapter(viewModel.languages) { language ->

                    //當一個語言被選中時
                    viewModel.languages.forEach { it.isSelected = false }
                    language.isSelected = true
                    selectionCallback(language)
                    builder.dismiss()

                }

            }

            builder.show()

        }

    }

}