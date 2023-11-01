package com.daniel.cathybktour.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.databinding.FragmentTourItemDetailBinding
import com.daniel.cathybktour.utils.viewBinding
import com.daniel.cathybktour.view.CommonWebViewActivity
import com.daniel.cathybktour.view.adapter.ImagePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TourItemDetailFragment : Fragment() {

    private val binding by viewBinding<FragmentTourItemDetailBinding>()
    private var tourItem: TourItem? = null

    companion object {

        private const val ARG_TOUR_ITEM = "tour_item"

        fun newInstance(tourItem: TourItem) = TourItemDetailFragment().apply {
            arguments = Bundle().apply {

                putParcelable(ARG_TOUR_ITEM, tourItem)

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tourItem = arguments?.getParcelable(ARG_TOUR_ITEM)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        initView()
        initListener()
        return binding.root

    }

    private fun initView() {

        binding.apply {

            toolbar.tvToolbarTitle.text = tourItem?.name

            layoutContent.apply {

                initSection(tvIntroduction, tourItem?.introduction)
                initSection(tvOpenTime, tourItem?.openTime, R.string.open_time)
                initSection(tvAddress, tourItem?.address, R.string.address)
                initSection(tvMobile, tourItem?.tel, R.string.mobile)
                initSection(tvWeb, tourItem?.officialSite, R.string.web_address)

                viewPager.adapter = ImagePagerAdapter(tourItem?.images)
                TabLayoutMediator(tlViewIndicator, viewPager) { _, _ -> }.attach()

            }

        }

    }

    private fun initSection(textView: TextView, content: String?, stringResId: Int? = null) {

        textView.apply {

            if (!content.isNullOrEmpty()) {

                visibility = View.VISIBLE
                text = stringResId?.let { getString(it, content) } ?: content

            } else {

                visibility = View.GONE

            }

        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun initListener() {

        binding.apply {

            toolbar.ivBack.setOnClickListener { parentFragmentManager.popBackStack() }

            layoutContent.apply {

                tvAddress.setOnClickListener {

                    val uri = Uri.parse("geo:0,0?q=${tourItem?.address}")
                    startActivity(Intent(Intent.ACTION_VIEW, uri).apply {

                        setPackage("com.google.android.apps.maps")

                    })

                }

                tvMobile.setOnClickListener {

                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tourItem?.tel}")))

                }

                tvWeb.setOnClickListener {

                    startActivity(Intent(activity, CommonWebViewActivity::class.java).apply {
                        putExtra("url", tourItem?.officialSite)
                        putExtra("title", tourItem?.name)

                    })

                }
            }
        }
    }
}


