package com.sunnyday.mvi

import android.util.Log
import androidx.activity.viewModels
import com.sunnyday.mvi.base.BaseActivity
import com.sunnyday.mvi.base.BaseViewAction
import com.sunnyday.mvi.base.UILoadState
import com.sunnyday.mvi.base.ViewModelFactory
import com.sunnyday.mvi.contract.MviLoginContract
import com.sunnyday.mvi.databinding.ActivityMviLoginBinding
import com.sunnyday.mvi.viewmodel.LoginViewModel

class MviLoginActivity : BaseActivity<MviLoginContract.ViewState, MviLoginContract.ViewEvent>() {

    private val viewModelFactory = ViewModelFactory(LoginViewModel())

    override val mBinding: ActivityMviLoginBinding by lazy {
        ActivityMviLoginBinding.inflate(layoutInflater)
    }

    override val mViewModel: MviLoginContract.ViewModel by viewModels {
        viewModelFactory
    }

    override fun initView() {
        mBinding.login.setOnClickListener {
            dispatchEvent(MviLoginContract.ViewEvent.Login("123456", "123456"))
        }
    }

    override fun onViewStateUpdate(viewState: MviLoginContract.ViewState) {
        when (viewState.loadState) {
            is UILoadState.LOADING -> {
                // show pb
            }
            is UILoadState.DATA -> {
                // close pb ,log 打印 加载成功回调
            }
            is UILoadState.ERROR -> {
                showToast("Login Failure~")
            }
        }
    }

    override fun onSideEffectAction(sideEffect: BaseViewAction.SideEffect<*>) {

    }

    override fun onDisplayScreenAction(displayScreen: BaseViewAction.DisplayScreen<*>) {
        when (displayScreen.screen) {
            is BaseViewAction.Screen.Main -> {
                // 登录成功，跳转Home页面
                showToast("Login Success，open Home Page~")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Test", "Test:onDestroy")
    }

}