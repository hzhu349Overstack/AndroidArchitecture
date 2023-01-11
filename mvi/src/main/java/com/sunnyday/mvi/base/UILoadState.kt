package com.sunnyday.mvi.base

/**
 * Create by SunnyDay /01/11 15:40:30
 * 定义UI加载状态这里定义了四种
 */
sealed class UILoadState {
    // 空数据状态，默认。
    object EMPTY : UILoadState()

    // 加载状态
    object LOADING : UILoadState()

    // 加载成功状态
    object DATA : UILoadState()

    //加载失败状态
    object ERROR : UILoadState()

}