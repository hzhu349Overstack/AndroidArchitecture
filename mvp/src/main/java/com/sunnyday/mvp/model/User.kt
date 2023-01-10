package com.sunnyday.mvp.model

/**
 * Create by SunnyDay /01/10 10:17:37
 * mvp中model还是原来的model。
 * 为了方便，这里可假定数据就是服务器存的User信息。
 */
data class User(val account: String = "admin",  val password: String = "123456")