package com.daniel.cathybktour.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daniel.cathybktour.databinding.FragmentExploreBinding
import com.daniel.cathybktour.utils.viewBinding

class ExploreFragment : Fragment() {

    private val binding by viewBinding<FragmentExploreBinding>()
    private val viewModel: MainActivityViewModel by activityViewModels()

    companion object {
        fun newInstance() = ExploreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = binding.root

        initView()

        return view

    }

    private fun initView() {

        binding.toolbar.ivBack.visibility = View.GONE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}