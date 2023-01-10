package com.sunnyday.mvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.sunnyday.mvvm.R
import com.sunnyday.mvvm.databinding.ActivityMvvmLoginBinding
import com.sunnyday.mvvm.bindingmodel.LoginBindingModel
import com.sunnyday.mvvm.model.User
import com.sunnyday.mvvm.viewmodel.LoginViewModel

class MvvmLoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMvvmLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_mvvm_login)

        // viewModel+liveData 实现监听回调
        loginViewModel.mutableLiveData.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
               t.let {
                   if (it==true){
                       Toast.makeText(this@MvvmLoginActivity,"login success",Toast.LENGTH_LONG).show()
                   }else{
                       Toast.makeText(this@MvvmLoginActivity,"login failure",Toast.LENGTH_LONG).show()
                   }
               }
            }
        })

        // login 事件，具体是在中ViewModel处理（View产生的数据会反馈到ViewModel上）
        binding.login.setOnClickListener {
            loginViewModel.login(binding.data!!.account,binding.data!!.password)
        }

        val model = LoginBindingModel()
        binding.data = model

        binding.changeDefaultValue.setOnClickListener {
            // 模拟触发触发逻辑，viewModel中数据变化
            val user = User(account = "123", password = "123")
            // 反馈到布局中
            model.password = user.account
            model.account = user.password
        }
    }
}