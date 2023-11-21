package com.daniel.cathybktour.view.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.DialogLanguageSelectionBinding
import com.daniel.cathybktour.databinding.FragmentHomeBinding
import com.daniel.cathybktour.model.Language
import com.daniel.cathybktour.utils.viewBinding
import com.daniel.cathybktour.view.adapter.LanguageAdapter
import com.daniel.cathybktour.view.adapter.TourAdapter
import com.daniel.cathybktour.view.main.TourItemDetailFragment
import com.daniel.cathybktour.view.main.viewModel.MainActivityViewModel
import com.jakewharton.rxbinding2.view.clicks
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private val binding by viewBinding<FragmentHomeBinding>()
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var tourAdapter: TourAdapter
    val llm = LinearLayoutManager(context)

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = binding.root
        initView()
        initData()

        return view

    }

    private fun initView() {

        binding.run {

            toolbar.run {
                ivBack.visibility = View.INVISIBLE
                llToolbarFeatures.visibility = View.VISIBLE
                tvToolbarTitle.text = getString(R.string.app_title)
            }

            layoutLoading.tvPleaseWait.text = getString(R.string.loading_please_wait)

            if (viewModel.totalDenominator.value != "0") {
                title.text = getString(R.string.attractions_title, "1", viewModel.totalDenominator.value)
            }
        }

    }

    private fun initData() {

        binding.recyclerview.apply {

            llm.orientation = LinearLayoutManager.VERTICAL
            layoutManager = llm

            //init adapter
            tourAdapter = TourAdapter(requireActivity(), itemClick = { selectedTourItem ->

                val fragment = TourItemDetailFragment.newInstance(selectedTourItem)
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,  //進入的動畫
                        R.anim.slide_out_left,  //退出的動畫
                        R.anim.slide_in_left,   //返回back
                        R.anim.slide_out_right  //當前 Fragment 退出的動畫（當按下返回鍵時或調用 popBackStack() 時）
                    )
                    .replace(R.id.home_fragment, fragment)
                    .addToBackStack(null)
                    .commit()

            }) { //更新完currentList後，做判斷

                tourAdapter.showFooter(tourAdapter.getAttractionsSize() != viewModel.totalDenominator.value)
                viewModel.setIsRvLoading(tourAdapter.getAttractionsSize() != viewModel.totalDenominator.value)

            }

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

                    if (viewModel.isRvLoading.value == true && dy > 0 && lastVisibleItem == totalItemCount - 1 && viewModel.currentLanguage.value?.code != "zh-tw") { //滑動到底部

                        viewModel.incrementPage()
                        viewModel.callApiTaipeiTour(viewModel.getSelectedLanguage(), viewModel.currentPage.value) //底部call api

                    }

                }
            })
        }

    }

    private fun initObserver() {
        //獲取主要資料
        viewModel.taipeiTourData.observe(viewLifecycleOwner) { tourModel ->

            if (viewModel.changeLanguageStatus.value == true) {

                tourAdapter.submitList(tourModel?.data) {

                    llm.scrollToPosition(0)

                }

                viewModel.changeLanguageStatus.value = false

            } else {

                tourAdapter.updateData(tourModel?.data)

            }

        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->

            binding.layoutLoading.apply {

                mainView.visibility = if (isError) View.VISIBLE else View.GONE
                pbLoadingGray.visibility = if (isError) View.GONE else View.VISIBLE
                clError.visibility = if (isError) View.VISIBLE else View.GONE
                tvError.text = getString(R.string.server_error)

            }

        }

        //observe是否loading頁面
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            binding.layoutLoading.apply {

                mainView.visibility = if (isLoading) View.VISIBLE else View.GONE
                cdLoading.visibility = View.VISIBLE
                clError.visibility = View.GONE

            }

        }

        //更改語言後判斷
        viewModel.currentLanguage.observe(viewLifecycleOwner) { language ->

            viewModel.changeLanguageStatus.value = true

            // 更新 UI
            initView()
            //假設語言切換到繁體中文，直接使用Room內的資料
            if (viewModel.currentLanguage.value?.code != "zh-tw") {

                viewModel.callApiTaipeiTour(language = language, viewModel.currentPage.value)//init call api && selected language call api

            } else {

                if (viewModel.allTourItems.value != null) {

                    viewModel.allTourItems.value.let { model ->


                        tourAdapter.submitList(model) {

                            llm.scrollToPosition(0)

                        }
                        viewModel.totalDenominator.value = model?.size.toString()
                        viewModel.setLoading(false)

                    }
                }


            }

        }

        //database
        viewModel.allTourItems.observe(viewLifecycleOwner) { model ->

            tourAdapter.submitList(model)
            viewModel.totalDenominator.value = model.size.toString()
            viewModel.setLoading(false)

        }

    }

    @SuppressLint("CheckResult")
    private fun initListener() {

        binding.toolbar.llToolbarFeatures.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {
            showLanguageDialog(requireContext()) { selectedLanguage ->

                viewModel.updateLanguage(selectedLanguage)

            }
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initListener()
        initObserver()

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