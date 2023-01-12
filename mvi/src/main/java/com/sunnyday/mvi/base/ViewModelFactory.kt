package com.sunnyday.mvi.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Create by SunnyDay /01/11 16:13:28
 * ViewModel工具类，传递一个ViewModel实例，方法返回对应的实例。结合viewModels使用。
 * 优雅的方案是使用Dagger注入。
 */
class ViewModelFactory<T>(private val viewModel: T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}