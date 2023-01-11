package com.sunnyday.mvi.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

/**
 * Create by SunnyDay /01/11 10:25:22
 * 1、ViewModel中处理完事件需要更新ViewSate给View层因此持有泛型VS
 * 2、ViewModel中不仅处理UI相关事件，还可能处理其他事件如activity跳转等逻辑，把结果回调，因此再定义个
 */
abstract class BaseViewModel<VS, VE>(viewState: VS) : ViewModel() {
    val stateFlow = MutableStateFlow(viewState)
    val actionFlow = MutableSharedFlow<BaseViewAction>()
    var mCurrentState = viewState
        set(value) {
            field = value
            stateFlow.value = value
        }

    abstract fun onViewEvent(event: VE)

    protected fun dispatchViewAction(action: BaseViewAction) =
        runBlocking {
            actionFlow.emit(action)
        }
}