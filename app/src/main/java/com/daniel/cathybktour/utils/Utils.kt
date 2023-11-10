package com.daniel.cathybktour.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.daniel.cathybktour.model.TourClusterItem
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker

fun vectorToBitmap(@DrawableRes id: Int, context: Context): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, id) as VectorDrawable
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


object Utils {

    fun dp2pixel(context: Context?, dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun setDrawableSelector(context: Context, normal: Int, selected: Int): Drawable {
        val stateNormal = ContextCompat.getDrawable(context, normal)
        val statePressed = ContextCompat.getDrawable(context, selected)
        val drawable = StateListDrawable()

        drawable.addState(intArrayOf(android.R.attr.state_selected), statePressed)
        drawable.addState(intArrayOf(android.R.attr.state_enabled), stateNormal)

        return drawable
    }

}