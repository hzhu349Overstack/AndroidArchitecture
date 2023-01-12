package com.sunnyday.mvi.binding

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Create by SunnyDay /01/12 11:09:01
 */
object ViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("isInvisible")
    fun View.setInvisible(invisible: Boolean) {
        visibility = if (invisible) View.INVISIBLE else View.VISIBLE
    }
}