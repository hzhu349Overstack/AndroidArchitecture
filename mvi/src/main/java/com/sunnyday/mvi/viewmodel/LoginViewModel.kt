package com.sunnyday.mvi.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sunnyday.mvi.base.BaseViewAction
import com.sunnyday.mvi.base.UILoadState
import com.sunnyday.mvi.contract.MviLoginContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Create by SunnyDay /01/11 16:20:49
 */
class LoginViewModel : MviLoginContract.ViewModel() {

    override fun onViewEvent(event: MviLoginContract.ViewEvent) {
        when (event) {
            is MviLoginContract.ViewEvent.Login -> {
                doLogin(event.account,event.password)
            }
        }
    }

    private fun doLogin(account:String,password:String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 模拟网络
            mCurrentState = mCurrentState.copy(loadState = UILoadState.LOADING)
            delay(2000)

            if (account=="admin"&&password=="123456"){
                mCurrentState = mCurrentState.copy(loadState = UILoadState.DATA)
                // 请求成功，跳转主页
                dispatchViewAction(BaseViewAction.DisplayScreen(BaseViewAction.Screen.Main))
            }else{
                // 请求网路失败
                mCurrentState = mCurrentState.copy(loadState = UILoadState.ERROR)
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Test","Test:onCleared")
    }
}