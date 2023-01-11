package com.sunnyday.mvi.base

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Create by SunnyDay /01/11 10:06:43
 * VS:UI层持有VS（ViewState），向ViewModel层发送VE（VE可认为是Event或Intent）
 * VE:可以是UI相关的动作，也可以是activity相关的动作。
 */
abstract class BaseActivity<VS, VE> : AppCompatActivity() {
    protected abstract val mBinding: ViewDataBinding
    protected abstract val mViewModel: BaseViewModel<VS, VE>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initView()
        lifecycleScope.launch {

            launch {
                mViewModel.stateFlow.collectLatest {
                    onViewStateUpdate(it)
                }
            }

            launch {
                mViewModel.actionFlow.collectLatest {
                    when (it) {
                        is BaseViewAction.SideEffect<*> -> onSideEffectAction(it)
                        is BaseViewAction.DisplayScreen<*> -> onDisplayScreenAction(it)
                        is BaseViewAction.Toast -> showToast(it.msg)
                        is BaseViewAction.CloseScreen -> finish()
                    }
                }
            }

        }
    }

    /**
     * 这做一些初始化工作
     * */
    abstract fun initView()

    /**
     *ViewState更新时这里进行更新UI
     * */
    abstract fun onViewStateUpdate(viewState: VS)

    /**
     * 处理ViewModel发来的ViewAction相关结果。具体是Action的细分SideEffectAction
     * */
    abstract fun onSideEffectAction(sideEffect: BaseViewAction.SideEffect<*>)

    abstract fun onDisplayScreenAction(displayScreen: BaseViewAction.DisplayScreen<*>)

    fun showToast(msg: String?) {
        Toast.makeText(applicationContext, msg ?: "none", Toast.LENGTH_SHORT).show()
    }

    fun dispatchEvent(event:VE) {
        mViewModel.onViewEvent(event)
    }
}