package com.sunnyday.mvvm.bindingmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sunnyday.mvvm.BR

/**
 * Create by SunnyDay /01/10 14:12:19
 */
class LoginBindingModel: BaseObservable() {


    @get:Bindable
    var account:String = "admin"
    set(value) {
        field = value
        notifyPropertyChanged(BR.account)
    }
    @get:Bindable
    var password:String = "123456"
    set(value) {
        field = value
        notifyPropertyChanged(BR.password)
    }
}