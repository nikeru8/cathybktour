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
import com.ncapdevi.fragnav.FragNavController
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var tourAdapter: TourAdapter


    //true 可以繼續往下讀取
    //false 阻擋
    private var isRvLoading = true

    var totalDenominator = "0"

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

    private val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.fl_content)


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


    private fun initView() {

        binding.toolbar.ivBack.visibility = View.INVISIBLE
        binding.toolbar.llToolbarFeatures.visibility = View.VISIBLE
        binding.toolbar.tvToolbarTitle.text = getString(R.string.app_title)
        binding.layoutLoading.tvPleaseWait.text = getString(R.string.loading_please_wait)

    }

    private fun initData() {

        //observe是否loading頁面
        viewModel.isLoading.observe(this) { isLoading ->

            binding.layoutLoading.mainView.visibility = if (isLoading) View.VISIBLE else View.GONE

        }


        viewModel.setIndex(1)
        viewModel.isLoading.value = true

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
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (totalDenominator != "0") binding.title.text =
                        getString(
                            R.string.attractions_title,
                            if (lastVisibleItem >= 0) lastVisibleItem.toString() else 0, //當選擇語言時，會遇到沒有item的情況，避免分母顯示成-1
                            totalDenominator
                        )
                    if (isRvLoading && dy > 0 && lastVisibleItem == totalItemCount - 1) { //滑動到底部

                        isRvLoading = false
                        viewModel.incrementPage()

                    }
                }
            })
        }

    }

    @SuppressLint("CheckResult")
    private fun initListener() {

        binding.toolbar.llToolbarFeatures.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {

            showLanguageDialog(this) { selectedLanguage ->

                val locale = Locale(selectedLanguage.code)
                val config = Configuration()
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)

                //更新語言
                initView()

                viewModel.isLoading.value = true
                tourAdapter.removeAll()
                viewModel.setLanguage(selectedLanguage.code)

            }

        }

    }

    private fun initObserver() {

        viewModel.isError.observe(this) { isError ->

            binding.layoutLoading.mainView.visibility = if (isError) View.VISIBLE else View.GONE
            binding.layoutLoading.pbLoadingGray.visibility = if (isError) View.GONE else View.VISIBLE
            binding.layoutLoading.clError.visibility = if (isError) View.VISIBLE else View.GONE
            binding.layoutLoading.tvError.text = "選擇語言伺服器異常，請稍後再試。"

        }

        //讀取資料
        viewModel.taipeiTourData.observe(this) { response ->

            if (response.isSuccessful) {

                viewModel.isLoading.value = true

                totalDenominator = response.body?.total.toString()

                response.body?.data?.let { tourAdapter.updateUser(it) }
                viewModel.isError.value = false
                viewModel.isLoading.value = false


            } else {

                viewModel.isLoading.value = false
                viewModel.isError.value = true

            }

            isRvLoading = true


        }

    }

    private fun showLanguageDialog(context: Context, selectionCallback: (Language) -> Unit) {

        val dialogBinding = DialogLanguageSelectionBinding.inflate(LayoutInflater.from(context))
        val recyclerView: RecyclerView = dialogBinding.recyclerView

        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.selected_language))
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        val adapter = LanguageAdapter(languages) { language ->

            //當一個語言被選中時
            languages.forEach { it.isSelected = false }
            language.isSelected = true

            selectionCallback(language)
            dialog.dismiss()

        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        dialog.show()
    }

}