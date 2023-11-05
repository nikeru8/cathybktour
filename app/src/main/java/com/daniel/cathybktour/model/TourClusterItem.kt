package com.daniel.cathybktour.model

import com.daniel.cathybktour.api.TourItem
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class TourClusterItem(var item: TourItem) : ClusterItem {

    override fun getPosition(): LatLng {
        return LatLng(item.nlat ?: 0.0, item.elong ?: 0.0)
    }

    override fun getTitle(): String? {
        return item.name
    }

    override fun getSnippet(): String? {
        return item.name
    }

}