package com.sunnyday.mvi.contract

import com.sunnyday.mvi.base.BaseViewModel
import com.sunnyday.mvi.base.UILoadState

/**
 * Create by SunnyDay /01/11 15:38:24
 */
interface MviLoginContract {

    // 定义个ViewModel基类，具体的实现类要自己实现。
    abstract class ViewModel : BaseViewModel<ViewState, ViewEvent>(ViewState())

    data class ViewState(val loadState: UILoadState = UILoadState.EMPTY)

    sealed class ViewEvent{
        data class Login(val account: String, val password: String) : ViewEvent()
    }
}