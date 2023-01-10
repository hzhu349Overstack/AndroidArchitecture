package com.sunnyday.mvp.presenter

import com.sunnyday.mvp.model.User

/**
 * Create by SunnyDay /01/10 10:24:39
 */
interface BasePresenter<V> {
    fun attachView(view: V)
    fun detachView()
    fun login(inputAccount:String,inputPassword:String,user: User)
}