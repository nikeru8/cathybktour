package com.daniel.cathybktour.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import androidx.core.content.ContextCompat

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