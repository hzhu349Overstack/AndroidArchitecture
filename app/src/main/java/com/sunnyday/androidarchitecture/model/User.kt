package com.sunnyday.androidarchitecture.model

/**
 * Create by SunnyDay /01/09 14:57:37
 * model 代表具体的数据，一般model来源自网络、数据库等，这里为了方便进行省略直接给了一个默认数据。
 */
data class User(val account: String = "admin",  val password: String = "123456")