# MVI

###### 1、回顾MVVM

想要MVVM的数据单向流动我们我们可能会碰到如下问题：

- 为了保证数据流的单向流动，LiveData向外暴露时需要转化成immutable的，这需要添加不少模板代码并且容易遗忘。
- View层通过调用ViewModel层的方法来交互的，View层与ViewModel的交互比较分散，不成体系。

###### 2、MVI简介

MVI与MVVM很相似，其借鉴了前端框架的思想，更加强调数据的单向流动和唯一数据源。

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvi1.png)

- Model：与MVVM中的Model不同的是，MVI的Model主要指UI状态（State）。例如页面加载状态、控件位置等都是一种UI状态
- View：Ui视图
- I：Intent，此Intent不是Activity的Intent，用户的任何操作都被包装成Intent后发送给Model层进行数据请求。

不过在设计时我们使用ViewModel来承担MVI的model层，这样整体架构与MVVM类似，主要区别是Model与View的交互部分

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvi2.png)

MVI强调数据的单向流动，主要分为以下几步，数据永远在一个环形结构中单向流动，不能反向流动：

（1）用户操作以Intent的形式通知ViewModel（向viewModel发送个Event事件）

（2）Model基于Intent更新State（ViewModel接受到事件处理事件，获取事件结果）

（3）View接收到State变化刷新UI（ViewModel吧事件结果回调给View，View中监听更新UI）

###### 3、MVI实现

（1）ViewModel基类

```kotlin
/**
 * Create by SunnyDay /01/11 10:25:22
 * 1、ViewModel中处理完事件需要更新ViewSate给View层因此持有泛型VS
 * 2、ViewModel中不仅处理UI相关事件，还可能处理其他事件如activity跳转等逻辑，把结果回调，因此再定义个
 */
abstract class BaseViewModel<VS, VE>(viewState: VS) : ViewModel() {
    // 两个flow分别用于发送ViewState，ViewAction事件。然后UI层监听数据的变化。
    val stateFlow = MutableStateFlow(viewState)
    val actionFlow = MutableSharedFlow<BaseViewAction>()

    var mCurrentState = viewState
        set(value) {
            field = value
            stateFlow.value = value
        }

    /**
     * 事件处理，处理UI层发送过来的事件。
     * */
    abstract fun onEvent(event: VE)

    /**
     * 像UI层发送action数据
     * */
    protected fun dispatchAction(action: BaseViewAction) =
        runBlocking {
            actionFlow.emit(action)
        }
}
```

（2）Activity基类

```kotlin
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
```

（3）Action基类

```kotlin
/**
 * Create by SunnyDay /01/11 14:58:58
 */
sealed class BaseViewAction {
    data class SideEffect<T>(val effect: T) : BaseViewAction()
    data class DisplayScreen<T>(val screen: T) : BaseViewAction()
    class Toast(val msg: String?) : BaseViewAction()
    object CloseScreen : BaseViewAction()

    sealed class Screen{
        object Main : Screen()
    }
}
```

（4）简单定义几个UI状态

```kotlin
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
```

###### 4、MVI栗子

上面基本已经把MVI的框架搭建完毕了，接下来便写个用户登录的例子练习下~

（1）UI层

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
      <variable
          name="data"
          type="com.sunnyday.mvi.binding.LoginBindModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".view.LoginActivity">

        <androidx.appcompat.widget.AppCompatEditText
            android:id="@+id/userAccount"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/user_account"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <androidx.appcompat.widget.AppCompatEditText
            android:id="@+id/userPassWord"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/user_password"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/userAccount" />

        <Button
            android:id="@+id/login"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/login"
            android:textAllCaps="false"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/userPassWord" />
        <!--isInvisible用到了DataBinding工具，参见工具代码-->
        <ProgressBar
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            android:id="@+id/pb"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:isInvisible="@{data.invisible}"/>

    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

```kotlin
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
```

（2）ViewModel层

```kotlin
/**
 * Create by SunnyDay /01/11 16:20:49
 */
class LoginViewModel : MviLoginContract.ViewModel() {

    override fun onEvent(event: MviLoginContract.ViewEvent) {
        when (event) {
            is MviLoginContract.ViewEvent.Login -> {
                doLogin(event.account,event.password)
            }
        }
    }

    private fun doLogin(account:String,password:String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 模拟网络
            mCurrentState = mCurrentState.copy(loadState = UILoadState.LOADING)
            delay(2000)

            if (account=="admin"&&password=="123456"){
                mCurrentState = mCurrentState.copy(loadState = UILoadState.DATA)
                // 请求成功，跳转主页
                dispatchAction(BaseViewAction.DisplayScreen(BaseViewAction.Screen.Main))
            }else{
                // 请求网路失败
                mCurrentState = mCurrentState.copy(loadState = UILoadState.ERROR)
            }

        }
    }
}
```

（3）契约接口约束

```kotlin
/**
 * Create by SunnyDay /01/11 15:38:24
 *
 * 定义契约类，方便管理每个UI对应的ViewModel，对自己的ViewModel进行约束。
 */
interface MviLoginContract {

    // 定义个ViewModel基类，具体的实现类要自己实现。
    abstract class ViewModel : BaseViewModel<ViewState, ViewEvent>(ViewState())

    data class ViewState(val loadState: UILoadState = UILoadState.EMPTY)

    sealed class ViewEvent{
        data class Login(val account: String, val password: String) : ViewEvent()
    }
}
```

（4）BindModel

```kotlin
/**
 * Create by SunnyDay /01/11 17:00:28
 * 注意使用@Bindable注解时要kt插件支持 apply plugin:'kotlin-kapt'
 */
class LoginBindModel : BaseObservable() {
    @Bindable
    var invisible: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.invisible)
        }
}
```

（5）栗子中用到的工具类

```kotlin
/**
 * Create by SunnyDay /01/12 11:09:01
 */
object ViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("isInvisible")
    fun View.setInvisible(invisible: Boolean) {
        visibility = if (invisible) View.INVISIBLE else View.VISIBLE
    }
}
```


```kotlin
/**
 * Create by SunnyDay /01/12 10:06:32
 */

/**
 * 工具类：替换DataBindingUtil.setContentView(activity, layoutRes)
 *
 * 用法：activity中 val binding: ActivityMainBinding by BindActivity(R.layout.activity_main)
 */
class BindActivity<in R : Activity, out T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : ReadOnlyProperty<R, T> {

    private var value: T? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            value = DataBindingUtil.setContentView<T>(thisRef, layoutRes)
        }
        return value!!
    }
}

/**
 * 工具类：替换DataBindingUtil.inflate(inflater, layoutRes,rootView, boolean)
 *
 * 用法：Fragment中 private val binding: FragmentMainBinding by BindFragment(R.layout.fragment_main)
 */
class BindFragment<in R : Fragment, out T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : ReadOnlyProperty<R, T> {

    private var value: T? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T {

        if (value == null) {
            value = DataBindingUtil.inflate(
                thisRef.layoutInflater, layoutRes,
                thisRef.view?.rootView as ViewGroup?, false
            )
        }
        return value!!
    }
}

/**
 * 工具类：替换DataBindingUtil.inflate(inflater, layoutRes,rootView, boolean)
 *
 * 用法:ViewGroup中 private val binding: ViewGroupBinding by BindViewGroup(R.layout.fragment_main)
 */
class BindViewGroup<in R : ViewGroup, out T : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : ReadOnlyProperty<R, T> {

    private var value: T? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            value = DataBindingUtil.inflate<T>(
                thisRef.layoutInflater, layoutRes,
                thisRef, true
            )
        }
        return value!!
    }
}

/**
 * 工具类：用于Recycler中RecyclerView.ViewHolder中的数据绑定
 *
 * 用法：  val binding: ItemPlanetBinding by BindView(view)
 */
class BindListItem<in R : RecyclerView.ViewHolder, out T : ViewDataBinding>(
    private val view: View
) : ReadOnlyProperty<R, T> {

    private var value: T? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T {
        if (value == null) {
            value = DataBindingUtil.bind(view)
        }
        return value!!
    }
}

val View.layoutInflater get() = context.getLayoutInflater()
fun Context.getLayoutInflater() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
```

其他详见mvi模块代码~

###### 4、感悟

再次来根据代码总结下架构图：

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvi3.png)

优点：

- View通过Action与ViewModel交互，通过Action通信，有利于View与ViewModel之间的进一步解耦，同时所有调用以Action的形式汇总到一处，也有利于对行为的集中分析和监控。
- MVI使用ViewState对State集中管理，只需要订阅一个ViewState便可获取页面的所有状态，相对MVVM减少了不少模板代码。

缺点：
- 所有的操作最终都会转换成State，所以当复杂页面的State容易膨胀
- State是不变的，因此每当State需要更新时都要创建新对象替代老对象，这会带来一定内存开销







