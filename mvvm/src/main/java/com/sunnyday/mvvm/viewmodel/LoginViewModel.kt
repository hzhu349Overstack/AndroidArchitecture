package com.sunnyday.mvvm.viewmodel

import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sunnyday.mvvm.model.User

/**
 * Create by SunnyDay /01/10 11:58:29
 */
class LoginViewModel : ViewModel() {

    val mutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun login(userInputAccount: String, userInputPassword: String) {
        val severInfo = User() // 模拟网络请求，假当这是从网络拿的数据。
        mutableLiveData.value =
            userInputAccount == severInfo.account && userInputPassword == severInfo.password
    }
}