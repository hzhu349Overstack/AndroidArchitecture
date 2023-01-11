package com.sunnyday.mvi.base

/**
 * Create by SunnyDay /01/11 14:58:58
 */
sealed class BaseViewAction {
    data class SideEffect<T>(val effect: T) : BaseViewAction()
    data class DisplayScreen<T>(val screen: T) : BaseViewAction()
    class Toast(val msg: String?) : BaseViewAction()
    object CloseScreen : BaseViewAction()

    sealed class Screen{
        object Main : Screen()
    }
}