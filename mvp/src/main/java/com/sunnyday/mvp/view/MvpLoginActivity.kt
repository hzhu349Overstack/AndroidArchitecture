package com.sunnyday.mvp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.sunnyday.mvp.R
import com.sunnyday.mvp.model.User
import com.sunnyday.mvp.presenter.BasePresenter
import com.sunnyday.mvp.presenter.LoginPresenterImpl
import kotlinx.android.synthetic.main.activity_mvp_login.*

class MvpLoginActivity : AppCompatActivity(), BaseView {
    private lateinit var loginPresenter: LoginPresenterImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvp_login)
        loginPresenter = LoginPresenterImpl().apply {
            attachView(this@MvpLoginActivity)
        }
        login.setOnClickListener {
            val inputAccount = userAccount.text.toString()
            val inputPassword = userPassWord.text.toString()
            val severUserInfo = User()
            loginPresenter.login(inputAccount, inputPassword, severUserInfo)
        }
    }

    override fun loginSuccess(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun loginFailure(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.detachView()
    }
}