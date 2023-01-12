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
    // 两个flow分别用于发送ViewState，ViewAction事件。然后UI层监听数据的变化。
    val stateFlow = MutableStateFlow(viewState)
    val actionFlow = MutableSharedFlow<BaseViewAction>()

    var mCurrentState = viewState
        set(value) {
            field = value
            stateFlow.value = value
        }

    /**
     * 事件处理，处理UI层发送过来的事件。
     * */
    abstract fun onEvent(event: VE)

    /**
     * 像UI层发送action数据
     * */
    protected fun dispatchAction(action: BaseViewAction) =
        runBlocking {
            actionFlow.emit(action)
        }
}