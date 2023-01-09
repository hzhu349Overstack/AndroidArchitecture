package com.sunnyday.androidarchitecture.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.sunnyday.androidarchitecture.R
import com.sunnyday.androidarchitecture.model.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val user = User()
        login.setOnClickListener {
            val account = userAccount.text.toString()
            val password = userPassWord.text.toString()
            if (account == user.account && password == user.password){
                Toast.makeText(applicationContext,"login success!",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext,"login failure!",Toast.LENGTH_LONG).show()
            }
        }
    }
}