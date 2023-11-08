package com.daniel.cathybktour.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.daniel.cathybktour.R
import com.daniel.cathybktour.api.TourItem
import com.daniel.cathybktour.databinding.FragmentExploreBinding
import com.daniel.cathybktour.model.TourClusterItem
import com.daniel.cathybktour.utils.PermissionUtils
import com.daniel.cathybktour.utils.viewBinding
import com.daniel.cathybktour.view.Dialog.TourDetailBottomSheet
import com.daniel.cathybktour.view.main.viewModel.ExploreFragmentViewModel
import com.daniel.cathybktour.view.main.viewModel.MainActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.jakewharton.rxbinding2.view.clicks
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class ExploreFragment : Fragment(), ClusterManager.OnClusterClickListener<TourClusterItem>,
    ClusterManager.OnClusterItemClickListener<TourClusterItem>, ClusterManager.OnClusterInfoWindowClickListener<TourClusterItem> {

    private val TAG = ExploreFragment::class.java.simpleName
    private val binding by viewBinding<FragmentExploreBinding>()
    private val viewModel: ExploreFragmentViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    /**
     *  指示在返回 [.onRequestPermissionsResult] 之後是否已拒絕請求的權限的標誌。
     */
    private var permissionGranted = false
    private lateinit var mMap: GoogleMap
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 15
    private val defaultLocation = LatLng(25.00996297, 121.455899)

    //確保api只被call一次
    private var hasCalledApi = false

    private var clusterManager: ClusterManager<TourClusterItem>? = null
    lateinit var clusterRenderer: HospitalRenderer
    private lateinit var previousItem: TourClusterItem
    private val mAllTypeList = mutableListOf<TourItem>()


    companion object {

        fun newInstance() = ExploreFragment()
        const val LOCATION_PERMISSION_REQUEST_CODE = 1

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = binding.root

        initData()
        initListener()
        initObserver()

        return view

    }

    private fun initObserver() {

        viewModel.taipeiTourData.observe(viewLifecycleOwner) { tourModel ->

            if (clusterManager != null)
                clusterManager?.clearItems()  // 清除所有 items

            // 添加新的 items
            tourModel?.data?.let { data ->
                mAllTypeList.clear()  //清空現有數據
                mAllTypeList.addAll(data)
                for (item in data) {
                    val tourClusterItem = TourClusterItem(item)
                    clusterManager?.addItem(tourClusterItem)
                }
            }

            // 在主線程中調用 cluster() 更新地圖
            Handler(Looper.getMainLooper()).post {
                clusterManager?.cluster()
            }

            // 確認這裡是在主線程中操作
            if (clusterManager == null) {
                setUpClusterer()  // 只在第一次設定
            }


        }

        mainActivityViewModel.currentLanguage.observe(viewLifecycleOwner) {

            initView()
            if (lastKnownLocation != null)
                viewModel.callApiTaipeiTour(
                    mainActivityViewModel.getSelectedLanguage(),
                    1,
                    lastKnownLocation!!.latitude,
                    lastKnownLocation!!.longitude
                )

        }

        viewModel.isError.observe(viewLifecycleOwner) { message ->

            Toast.makeText(requireContext(), "explore = $message", Toast.LENGTH_SHORT).show()

        }

        viewModel.locationError.observe(viewLifecycleOwner) { event ->

            event.getContentIfNotHandled()?.let { error ->
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show()
            }

        }

        //獲取地圖位置
        viewModel.locationData.observe(viewLifecycleOwner) { location ->

            try {

                lastKnownLocation = location

                if (permissionGranted) {
                    // 使用 location 來更新 UI
                    location?.let {
                        // 更新地圖的攝像機位置
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude),
                                DEFAULT_ZOOM.toFloat()
                            )
                        )

                        // 檢查是否需要調用 API
                        if (!hasCalledApi) {
                            hasCalledApi = true
                            viewModel.callApiTaipeiTour(
                                mainActivityViewModel.getSelectedLanguage(),
                                1,
                                it.latitude,
                                it.longitude
                            )
                        }
                    } ?: run {
                        // 位置為 null 時的處理，例如顯示 Toast 或將地圖移動到默認位置
                        Toast.makeText(activity, "請開啟精準定位，才能獲取您的所在位置喔！", Toast.LENGTH_SHORT).show()
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                    }

                } else {

                    mMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                    )


                }
            } catch (e: SecurityException) {

                Log.e("Exception: %s", e.message, e)

            }

        }

        // 觀察地圖準備
        viewModel.mapReady.observe(viewLifecycleOwner) { googleMap ->

            // 配置地圖
            googleMap?.let { map ->

                // 如果 googleMap 不是 null，則進入此塊
                mMap = map
                updateLocationUI()

            } ?: run {

                // 如果 googleMap 是 null
                Log.e(TAG, "GoogleMap is null")

            }

        }

    }


    private fun initView() {

        binding.toolbar.tvToolbarTitle.text = resources.getStringArray(R.array.tab_main)[1]
        binding.toolbar.ivBack.visibility = View.INVISIBLE

    }

    private fun initData() {


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        viewModel.initMap(mapFragment)
        mapFragment.getMapAsync { googleMap ->

        }

    }

    @SuppressLint("CheckResult")
    private fun initListener() {

        binding.ivMyLocation.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {

            if (hasLocationPermission()) {

                checkPermission()
                viewModel.getDeviceLocation()

            } else {

                Toast.makeText(requireContext(), getString(R.string.permission_required_toast), Toast.LENGTH_SHORT).show()

            }

        }

    }


    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {

        if (mMap == null) {

            return
        }

        try {
            if (permissionGranted) {

                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
                viewModel.getDeviceLocation()

            } else {

                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                checkPermission()

            }
        } catch (e: SecurityException) {

            Log.e("Exception: %s", e.message, e)

        }

    }

    @SuppressLint("MissingPermission")
    private fun checkPermission() {
        //  1. 檢查是否已授予權限，如果已授予，則啟用我的位置圖層
        if (hasLocationPermission()) {

            permissionGranted = true
            mMap.isMyLocationEnabled = true
            viewModel.getDeviceLocation()
            return

        }

        // 2. 應顯示權限說明對話框
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            PermissionUtils.RationaleDialog.newInstance(LOCATION_PERMISSION_REQUEST_CODE, true)
                .show(childFragmentManager, "dialog")
            return

        }

        // 問用戶授權
        requestPermissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )

    }

    //設置聚合功能
    @SuppressLint("PotentialBehaviorOverride")
    private fun setUpClusterer() {

        clusterManager = ClusterManager(requireContext(), mMap)
        clusterRenderer = HospitalRenderer(requireActivity())
        clusterRenderer.minClusterSize = 3
        clusterManager!!.renderer = clusterRenderer

        val metrics = DisplayMetrics()

        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        clusterManager!!.setAlgorithm(
            NonHierarchicalViewBasedAlgorithm(
                metrics.widthPixels, metrics.heightPixels
            )
        )

        clusterManager!!.setOnClusterClickListener(this)
        clusterManager!!.setOnClusterItemClickListener(this)

        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)


        mMap.setOnCameraMoveStartedListener { reason ->
            // 1 移動 使用者手勢
            // 2 使用者點擊螢幕
            // 3 動畫

        }

        for (item in mAllTypeList) {

            val tempItem = TourClusterItem(item)
            clusterManager!!.addItem(tempItem)

        }

        clusterManager!!.setAnimation(false)
        clusterManager!!.cluster()

    }

    inner class HospitalRenderer(activity: Activity) : DefaultClusterRenderer<TourClusterItem>(activity, mMap, clusterManager!!) {


        override fun onClusterItemRendered(clusterItem: TourClusterItem, marker: Marker) {
            super.onClusterItemRendered(clusterItem, marker)

        }

        override fun onBeforeClusterItemRendered(model: TourClusterItem, markerOptions: MarkerOptions) {
            // Draw a single person - show their profile photo and set the info window to show their
            //用於判斷種類
            markerOptions
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                .title(model.title)

        }

        //更新選中的Marker
        fun setUpdateSelectedMarker(model: TourClusterItem) {

            try {

                val marker: Marker = getMarker(model)
                if (marker != null) {

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pic_selected))
                    clusterManager?.cluster()

                }

            } catch (e: Exception) {

                Log.e(TAG, "setUpdateMarker get error - ${e.message} , model hospName - ${model.item.name}")

            }


        }

        //替換回原本樣子
        fun reloadMarker(model: TourClusterItem) {

            try {

                val marker: Marker = getMarker(model)
                if (marker != null) {

                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                    clusterManager?.cluster()

                }

            } catch (e: Exception) {

                Log.e(TAG, "reloadMarker get error - ${e.message}")

            }


        }

        override fun onClusterItemUpdated(item: TourClusterItem, marker: Marker) {
            super.onClusterItemUpdated(item, marker)
        }

    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                // 權限被用戶授予
                permissionGranted = true
                updateLocationUI()

            } else {
                // 權限被用戶拒絕
                checkPermission()
            }
        }

    override fun onClusterClick(cluster: Cluster<TourClusterItem>?): Boolean {

        val builder = LatLngBounds.builder()
        for (item in cluster?.items!!) {
            builder.include(item.position)
        }

        val bounds = builder.build()

        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true

    }

    override fun onClusterItemClick(item: TourClusterItem?): Boolean {

        item?.let {

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(item.item.nlat!!, item.item.elong!!), 18.0f))

            val tourDetailBottomSheet = TourDetailBottomSheet.newInstance(item.item)
            tourDetailBottomSheet.show(requireActivity().supportFragmentManager, "tour_detail")

            clusterRenderer.setUpdateSelectedMarker(item)

            //更換之前點選的icon
            if (this::previousItem.isInitialized && previousItem.position != item.position) {

                clusterRenderer.reloadMarker(previousItem)

            }

            previousItem = item

        }

        return true
    }

    override fun onClusterInfoWindowClick(cluster: Cluster<TourClusterItem>?) {
        Toast.makeText(activity, "cluster ${cluster?.items?.size}}", Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy()
    {
        clusterRenderer.onRemove()
        clusterManager = null
        super.onDestroy()

    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}