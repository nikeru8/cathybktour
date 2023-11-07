package com.daniel.cathybktour.view.Dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.databinding.TourMapInfoBinding
import com.daniel.cathybktour.view.adapter.ImagePagerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso

class TourDetailBottomSheet : BottomSheetDialogFragment() {

    private val TAG = TourDetailBottomSheet::class.java.simpleName
    private var binding: TourMapInfoBinding? = null  // 假設你的布局名稱是tour_map_info

    companion object {

        private const val ARG_ITEM = "item"

        fun newInstance(tourItem: TourItem): TourDetailBottomSheet {
            val fragment = TourDetailBottomSheet()
            val args = Bundle()
            args.putParcelable(ARG_ITEM, tourItem)
            fragment.arguments = args
            return fragment
        }

    }


    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) ?: return
        val behavior = BottomSheetBehavior.from(bottomSheet)
        var lastState = BottomSheetBehavior.STATE_HALF_EXPANDED

        // 初始化peekHeight為螢幕高度的1/4
        behavior.peekHeight = resources.displayMetrics.heightPixels / 4
        // 初始化狀態為半展開狀態
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                // 記錄除了拖動和定置之外的最新狀態
                if (newState != BottomSheetBehavior.STATE_DRAGGING && newState != BottomSheetBehavior.STATE_SETTLING) {
                    lastState = newState
                }

                // 僅當用戶放手，且上一個狀態為全屏時，才執行到半展開
                if (newState == BottomSheetBehavior.STATE_SETTLING && lastState == BottomSheetBehavior.STATE_EXPANDED) {
                    Log.d(TAG, "用戶釋放從全屏狀態，移動到半展開")
                    bottomSheet.post {
                        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d(TAG, "onSlide: slideOffset = $slideOffset")
                // 不做任何狀態的記錄，只記錄滑動情況
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = TourMapInfoBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        // 從arguments中取得傳遞過來的TourItem
        arguments?.let {
            val tourItem: TourItem = it.getParcelable(ARG_ITEM)!!
            // 現在你可以使用這個tourItem來更新UI了
            updateUI(tourItem)
            initListener(tourItem)

        }


    }

    private fun initView() {

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet as View)
        // 在 initView 中設定初始狀態為半開狀態
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

    }


    private fun initListener(item: TourItem) {

        binding?.ivClose?.setOnClickListener {

            dismiss()

        }

        binding?.tvAddress?.setOnClickListener {

            val uri = Uri.parse("geo:0,0?q=${item.address}")
            startActivity(Intent(Intent.ACTION_VIEW, uri).apply {

                setPackage("com.google.android.apps.maps")

            })

        }

    }

    private fun updateUI(item: TourItem) {
        binding?.apply {

            tvName.text = item.name
            tvAddress.text = getString(R.string.guild_there, item.address)
            tvIntroduction.text = item.introduction

            if (item.images.isNullOrEmpty() || item.images.firstOrNull()?.src.isNullOrEmpty()) {
                // 如果沒有圖片或第一個圖片來源是空的，則加載默認圖片
                Picasso.get().load(R.drawable.taipei_icon).into(ivCircleImage)
            } else {
                // 如果有圖片，且第一個圖片元素的來源不是空的，則加載該圖片
                Picasso.get().load(item.images[0]?.src).into(ivCircleImage)
            }

            viewPager.adapter = ImagePagerAdapter(item.images ?: null)
            TabLayoutMediator(tlViewIndicator, viewPager) { _, _ -> }.attach()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null

    }
}
