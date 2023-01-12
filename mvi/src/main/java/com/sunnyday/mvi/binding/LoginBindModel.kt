package com.sunnyday.mvi.binding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sunnyday.mvi.BR

/**
 * Create by SunnyDay /01/11 17:00:28
 * 注意使用@Bindable注解时要kt插件支持 apply plugin:'kotlin-kapt'
 */
class LoginBindModel : BaseObservable() {
    @Bindable
    var invisible: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.invisible)
        }
}