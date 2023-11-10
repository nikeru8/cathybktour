package com.daniel.cathybktour.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.daniel.cathybktour.databinding.FragmentNewsBinding
import com.daniel.cathybktour.utils.Result
import com.daniel.cathybktour.utils.viewBinding
import com.daniel.cathybktour.view.main.viewModel.MainActivityViewModel
import com.daniel.cathybktour.view.main.viewModel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val binding by viewBinding<FragmentNewsBinding>()

    companion object {
        fun newInstance() = NewsFragment()
    }

    private val viewModel: NewsViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = binding.root

        initView()
        initData()
        initListener()
        initObserver()

        return view

    }

    private fun initView() {

        binding.toolbar.ivBack.visibility = View.INVISIBLE

    }

    private fun initData() {

        viewModel.fetchNews(mainActivityViewModel.currentLanguage.value?.code, 1)

    }


    private fun initListener() {


    }

    private fun initObserver() {

        viewModel.taipeiNews.observe(viewLifecycleOwner) { result ->

            when (result) {

                is Result.success -> {

                    Log.d("TAG", "sssss call api success")
                    binding.tvHello.text = result.data.toString()

                }

                is Result.failure -> {

                    Log.d("TAG", "sssss call api failure")

                }

                is Result.loading -> {

                    Log.d("TAG", "sssss call api loading")

                }

            }

        }

        mainActivityViewModel.currentLanguage.observe(viewLifecycleOwner) {

            viewModel.fetchNews(it.code, 1)

        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}