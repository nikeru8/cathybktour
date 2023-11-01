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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daniel.cathybktour.R
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

        // 1. 初始化工具列
        initToolbar()

        // 2. 初始化介紹文字段落
        initSection(binding.layoutContent.tvIntroduction, tourItem?.introduction)

        // 3. 初始化開放時間
        initSection(binding.layoutContent.tvOpenTime, tourItem?.openTime, R.string.open_time)

        // 4. 初始化地址
        initSection(binding.layoutContent.tvAddress, tourItem?.address, R.string.address)

        // 5. 初始化聯絡電話
        initSection(binding.layoutContent.tvMobile, tourItem?.tel, R.string.mobile)

        // 6. 初始化官方網站
        initSection(binding.layoutContent.tvWeb, tourItem?.officialSite, R.string.web_address)

        // 7. 初始化圖片展示區
        initImageSection()

    }

    private fun initSection(textView: TextView, content: String?, stringResId: Int? = null) {

        if (!content.isNullOrEmpty()) {

            textView.visibility = View.VISIBLE
            textView.text = stringResId?.let { getString(it, content) } ?: content

        } else {

            textView.visibility = View.GONE

        }

    }

    private fun initToolbar() {
        binding.toolbar.tvToolbarTitle.text = tourItem?.name
    }


    private fun initImageSection() {

        val imageAdapter = if (tourItem?.images == null || tourItem?.images?.isEmpty() == true) {
            ImagePagerAdapter()  // 這將使用默認圖片
        } else {
            ImagePagerAdapter(tourItem?.images ?: mutableListOf())
        }

        binding.layoutContent.viewPager.adapter = imageAdapter

        // 連接指示器與ViewPager
        TabLayoutMediator(binding.layoutContent.tlViewIndicator, binding.layoutContent.viewPager) { _, _ -> }.attach()

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


