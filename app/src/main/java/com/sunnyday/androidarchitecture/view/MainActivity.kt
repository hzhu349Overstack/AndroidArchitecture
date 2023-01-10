package com.sunnyday.androidarchitecture.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sunnyday.androidarchitecture.R
import com.sunnyday.mvp.view.MvpLoginActivity
import com.sunnyday.mvvm.ui.MvvmLoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mvc.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        mvp.setOnClickListener {
            startActivity(Intent(this,MvpLoginActivity::class.java))
        }
        mvvm.setOnClickListener {
            startActivity(Intent(this, MvvmLoginActivity::class.java))
        }
        mvi.setOnClickListener {

        }
    }
}