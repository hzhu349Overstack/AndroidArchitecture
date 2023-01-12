package com.sunnyday.mvi

import android.util.Log
import androidx.activity.viewModels
import com.sunnyday.mvi.base.BaseActivity
import com.sunnyday.mvi.base.BaseViewAction
import com.sunnyday.mvi.base.UILoadState
import com.sunnyday.mvi.base.ViewModelFactory
import com.sunnyday.mvi.binding.LoginBindModel
import com.sunnyday.mvi.contract.MviLoginContract
import com.sunnyday.mvi.databinding.ActivityMviLoginBinding
import com.sunnyday.mvi.delegates.BindActivity
import com.sunnyday.mvi.viewmodel.LoginViewModel

class MviLoginActivity : BaseActivity<MviLoginContract.ViewState, MviLoginContract.ViewEvent>() {

    private val viewModelFactory = ViewModelFactory(LoginViewModel())

    override val mBinding: ActivityMviLoginBinding by BindActivity(R.layout.activity_mvi_login)

    override val mViewModel: MviLoginContract.ViewModel by viewModels {
        viewModelFactory
    }

    override fun initView() {
        mBinding.login.setOnClickListener {
            dispatchEvent(MviLoginContract.ViewEvent.Login("123456", "123456"))
        }
        mBinding.data = LoginBindModel()
    }

    override fun onViewStateUpdate(viewState: MviLoginContract.ViewState) {
        when (viewState.loadState) {

            is UILoadState.EMPTY -> {
                // 初始化，ViewModel初始化的时候会发送一个事件
                Log.d("MviLoginActivity","UILoadState.EMPTY")
                mBinding.data!!.invisible=true
            }

            is UILoadState.LOADING -> {
                // show pb
                // 可使用DataBinding 与可观察的实体类绑定。比如定义个Boolean类来控制pb显示隐藏：
                // 1、这里UILoadState为EMPTY、DATA，ERROR pb隐藏
                // 2、这里UILoadState为LOADING pb显示
                mBinding.data!!.invisible=false
            }
            is UILoadState.DATA -> {
                // close pb
                // may do something here
                mBinding.data!!.invisible=true
            }
            is UILoadState.ERROR -> {
                showToast("Login Failure~")
                mBinding.data!!.invisible=true
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