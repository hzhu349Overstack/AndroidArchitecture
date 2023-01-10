package com.sunnyday.mvp.presenter

import com.sunnyday.mvp.model.User
import com.sunnyday.mvp.view.BaseView

/**
 * Create by SunnyDay /01/10 10:26:34
 */
class LoginPresenterImpl : BasePresenter<BaseView> {
    private var mView: BaseView? = null
    override fun attachView(view: BaseView) {
        this.mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun login(inputAccount:String,inputPassword:String,user: User) {
        mView?.let {
            if (user.account == inputAccount && user.password == inputPassword) {
                it.loginSuccess("login success")
            } else {
                it.loginFailure("login failure")
            }
        }
    }
}