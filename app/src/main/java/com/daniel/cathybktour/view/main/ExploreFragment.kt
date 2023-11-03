package com.daniel.cathybktour.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daniel.cathybktour.R
import com.daniel.cathybktour.databinding.FragmentExploreBinding
import com.daniel.cathybktour.utils.PermissionUtils
import com.daniel.cathybktour.utils.viewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.jakewharton.rxbinding2.view.clicks
import java.util.concurrent.TimeUnit


class ExploreFragment : Fragment() {

    private val TAG = ExploreFragment::class.java.simpleName
    private val binding by viewBinding<FragmentExploreBinding>()
    private val viewModel: MainActivityViewModel by activityViewModels()

    /**
     *  指示在返回 [.onRequestPermissionsResult] 之後是否已拒絕請求的權限的標誌。
     */
    private var permissionGranted = false
    private lateinit var mMap: GoogleMap
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val DEFAULT_ZOOM = 15
    private val defaultLocation = LatLng(25.00996297, 121.455899)

    companion object {

        fun newInstance() = ExploreFragment()
        const val LOCATION_PERMISSION_REQUEST_CODE = 1

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = binding.root

        initView()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->

            mMap = googleMap
            updateLocationUI()

        }

        initListener()

        return view

    }

    @SuppressLint("CheckResult")
    private fun initListener() {

        binding.ivMyLocation.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe {

            checkPermission()
            getDeviceLocation() //initListener

        }

    }

    private fun initView() {

        binding.toolbar.ivBack.visibility = View.GONE

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
                getDeviceLocation() //updateLocationUI


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
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            permissionGranted = true
            mMap.isMyLocationEnabled = true
            getDeviceLocation()//checkPermission
            return

        }

        // 2. 應顯示權限說明對話框
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(childFragmentManager, "dialog")

            return

        }


        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // [END maps_check_location_permission]
    }

    private fun getDeviceLocation() {

        /*
         * 獲取設備的最佳和最新位置，這在某些罕見情況下可能為null，即位置不可用。
         */
        try {

            if (permissionGranted) {

                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {

                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {

                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        } else {

                            Toast.makeText(activity, "請開啟精準定位，才能獲取您的所在位置喔！", Toast.LENGTH_SHORT).show()
                            mMap.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                            )

                        }

                    } else {

                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
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

}