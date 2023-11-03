package com.daniel.cathybktour.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.FragmentExploreBinding
import com.daniel.cathybktour.utils.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng


class ExploreFragment : Fragment() {

    private val binding by viewBinding<FragmentExploreBinding>()
    private val viewModel: MainActivityViewModel by activityViewModels()

    companion object {
        fun newInstance() = ExploreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = binding.root

        initView()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(25.0330, 121.5654),
                    15.0f
                )
            )

        }

        return view

    }

    private fun initView() {

        binding.toolbar.ivBack.visibility = View.GONE

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}