package com.sunnyday.mvi.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Create by SunnyDay /01/11 16:13:28
 */
class ViewModelFactory<T>(private val viewModel: T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}