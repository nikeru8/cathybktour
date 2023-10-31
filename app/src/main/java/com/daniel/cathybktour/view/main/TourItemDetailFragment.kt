package com.daniel.cathybktour.view.main

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.Image
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.databinding.FragmentTourItemDetailBinding
import com.daniel.cathybktour.view.CommonWebViewActivity
import com.daniel.cathybktour.view.adapter.ImagePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TourItemDetailFragment : Fragment() {

    private var TAG = TourItemDetailFragment::class.java.simpleName

    private lateinit var binding: FragmentTourItemDetailBinding

    private var tourItem: TourItem? = null
    private val viewModel: MainActivityViewModel by activityViewModels()

    companion object {

        private const val ARG_TOUR_ITEM = "tour_item"

        fun newInstance(tourItem: TourItem): TourItemDetailFragment {
            return TourItemDetailFragment().apply {
                arguments = Bundle().apply {

                    putParcelable(ARG_TOUR_ITEM, tourItem)

                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            tourItem = it.getParcelable(ARG_TOUR_ITEM)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTourItemDetailBinding.inflate(inflater, container, false)

        Log.d(TAG, "tourItem - $tourItem")
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {

        binding.toolbar.tvToolbarTitle.text = tourItem?.name

        if (!tourItem?.introduction.isNullOrEmpty()) {
            binding.layoutContent.tvIntroduction.visibility = View.VISIBLE
            binding.layoutContent.tvIntroduction.text = tourItem?.introduction
        } else {
            binding.layoutContent.tvIntroduction.visibility = View.GONE
        }

        if (!tourItem?.openTime.isNullOrEmpty()) {
            binding.layoutContent.tvOpenTime.visibility = View.VISIBLE
            binding.layoutContent.tvOpenTime.text = getString(R.string.open_time, tourItem?.openTime)
        } else {
            binding.layoutContent.tvOpenTime.visibility = View.GONE
        }

        if (!tourItem?.address.isNullOrEmpty()) {
            binding.layoutContent.tvAddress.visibility = View.VISIBLE
            binding.layoutContent.tvAddress.text = getString(R.string.address, tourItem?.address)
        } else {
            binding.layoutContent.tvAddress.visibility = View.GONE
        }

        if (!tourItem?.tel.isNullOrEmpty()) {
            binding.layoutContent.tvMobile.visibility = View.VISIBLE
            binding.layoutContent.tvMobile.text = getString(R.string.mobile, tourItem?.tel)
        } else {
            binding.layoutContent.tvMobile.visibility = View.GONE
        }

        if (!tourItem?.officialSite.isNullOrEmpty()) {
            binding.layoutContent.tvWeb.visibility = View.VISIBLE
            binding.layoutContent.tvWeb.text = getString(R.string.web_address, tourItem?.officialSite ?: "")
        } else {
            binding.layoutContent.tvWeb.visibility = View.GONE
        }


        if (tourItem?.images?.isEmpty() == true)
            tourItem?.images?.add(Image(".jpg", "https://t4.ftcdn.net/jpg/04/73/25/49/360_F_473254957_bxG9yf4ly7OBO5I0O5KABlN930GwaMQz.jpg", ""))
        val imageAdapter = ImagePagerAdapter(tourItem?.images)
        binding.layoutContent.viewPager.adapter = imageAdapter

        //indicator 連動
        TabLayoutMediator(binding.layoutContent.tlViewIndicator, binding.layoutContent.viewPager) { tab, position ->

        }.attach()

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun initListener() {

        binding.toolbar.ivBack.setOnClickListener {

            parentFragmentManager.popBackStack()

        }

        binding.layoutContent.tvAddress.setOnClickListener {

            // 嘗試使用 Intent 開啟 Google Maps App
            try {

                val uri = Uri.parse("geo:0,0?q=${tourItem?.address}")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

            } catch (e: ActivityNotFoundException) {

                // 如果用戶的手機沒有安裝 Google Maps，則開啟網頁版本
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${tourItem?.address}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)

            }

        }

        binding.layoutContent.tvMobile.setOnClickListener {

            val phoneNumber = tourItem?.tel  // 這裡換成你想要的電話號碼

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")

            // 檢查裝置是否有支援的應用程式來處理這個 Intent
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {

                Toast.makeText(activity, "沒有找到撥打電話的應用程式", Toast.LENGTH_SHORT).show()

            }

        }

        binding.layoutContent.tvWeb.setOnClickListener {

            val intent = Intent(activity, CommonWebViewActivity::class.java).apply {

                this.putExtra("url", tourItem?.officialSite)
                this.putExtra("title", tourItem?.name)

            }

            startActivity(intent)

        }

    }

}


