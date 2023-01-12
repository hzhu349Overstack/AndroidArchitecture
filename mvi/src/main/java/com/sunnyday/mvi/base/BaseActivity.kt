package com.sunnyday.mvi.base

import android.os.Bundle
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
     * 处理ViewModel发来的ViewAction相关结果。具体是Action的细分SideEffectAction。
     *
     * 如下：
     * onDisplayScreenAction也是类似的原理，其实还有种做法就是把这些action进行统一放置如取个方法名字叫onAction
     * 那么方法内部就要通过when进行分类判断BaseViewAction的不同子类类型了。
     *
     * 两种方式根据自己喜欢和实际情况自行取舍。
     * */
    abstract fun onSideEffectAction(sideEffect: BaseViewAction.SideEffect<*>)

    /**
     * 屏幕跳转专门处理，其他类型一般都放到了SideEffectAction中，认为都是SideEffect
     * 因此UI层我们划分了三种类型：
     * 1、ViewStateUpdate，专门处理UI刷新。
     * 2、DisplayScreenAction，专门处理页面跳转。
     * 3、SideEffectAction其他的事件
     * */
    abstract fun onDisplayScreenAction(displayScreen: BaseViewAction.DisplayScreen<*>)

    fun showToast(msg: String?) {
        Toast.makeText(applicationContext, msg ?: "none", Toast.LENGTH_SHORT).show()
    }

    /**
     * 向ViewModel发送一个事件，调用ViewModel的onEvent
     * 这个事件触发后，可能会回调更新UI，也可能会回调处理activity的其他事情比如跳转。
     * */
    fun dispatchEvent(event:VE) {
        mViewModel.onEvent(event)
    }
}