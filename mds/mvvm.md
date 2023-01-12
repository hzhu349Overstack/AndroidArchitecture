# mvvm

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvvm.png)

###### 1、介绍

- M：Model，数据模型。
- V：View，视图模型。
- VM：ViewModel：处理业务逻辑，model与view的桥梁。model与View通过ViewModel进行沟通。


MVVM 模式将 Presenter 改名为 ViewModel，基本上与 MVP 模式完全一致。唯一的区别是，它采用双向数据绑定（data-binding）：View的变动，自动反映在 ViewModel，反之亦然。

###### 2、实现

（1）model

```kotlin
/**
 * Create by SunnyDay /01/10 15:40:37
 */
data class User(val account: String = "admin",  val password: String = "123456")
```

（2）view

```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="data"
            type="com.sunnyday.mvvm.bindingmodel.LoginBindingModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".ui.MvvmLoginActivity">

        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilAccount"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/userAccount"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="@string/user_account"
                android:text="@={data.account}" />
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/tilPassword"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/tilAccount">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/userPassWord"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="@string/user_password"
                android:text="@={data.password}" />
        </com.google.android.material.textfield.TextInputLayout>

        <Button
            android:id="@+id/login"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/login"
            android:textAllCaps="false"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/tilPassword" />

        <Button
            android:id="@+id/changeDefaultValue"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="changeDefaultValue"
            android:textAllCaps="false"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@id/login" />


    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

```kotlin
class MvvmLoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMvvmLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_mvvm_login)

        // viewModel+liveData 实现监听回调
        loginViewModel.mutableLiveData.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
               t.let {
                   if (it==true){
                       Toast.makeText(this@MvvmLoginActivity,"login success",Toast.LENGTH_LONG).show()
                   }else{
                       Toast.makeText(this@MvvmLoginActivity,"login failure",Toast.LENGTH_LONG).show()
                   }
               }
            }
        })

        // login 事件，具体是在中ViewModel处理（View产生的数据会反馈到ViewModel上）
        binding.login.setOnClickListener {
            loginViewModel.login(binding.data!!.account,binding.data!!.password)
        }

        val model = LoginBindingModel()
        binding.data = model

        binding.changeDefaultValue.setOnClickListener {
            // 模拟触发触发逻辑，viewModel中数据变化
            val user = User(account = "123", password = "123")
            // 反馈到布局中
            model.password = user.account
            model.account = user.password
        }
    }
}
```

（3）ViewModel

```kotlin
/**
 * Create by SunnyDay /01/10 11:58:29
 */
class LoginViewModel : ViewModel() {

    val mutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun login(userInputAccount: String, userInputPassword: String) {
        val severInfo = User() // 模拟网络请求，假当这是从网络拿的数据。
        mutableLiveData.value =
            userInputAccount == severInfo.account && userInputPassword == severInfo.password
    }
}
```

（4）binding

```kotlin
/**
 * Create by SunnyDay /01/10 14:12:19
 */
class LoginBindingModel: BaseObservable() {

    @get:Bindable
    var account:String = "admin"
    set(value) {
        field = value
        notifyPropertyChanged(BR.account)
    }
    @get:Bindable
    var password:String = "123456"
    set(value) {
        field = value
        notifyPropertyChanged(BR.password)
    }
}
```

###### 3、总结

MVVM使用数据绑定来实现，Google官方提供了一个Ui框架DataBinding库。可能很多人不喜欢这个库，然后实现上就使用了viewModel+数据监听方案 来实现view与model双向通信了。实现效果就是上图架构图中的下面那副了。这样是有弊端的：

- View观察ViewModel的数据变化并自我更新，这其实是单一数据源而不是双向数据绑定，所以其实MVVM的这一大特性我其实并没有用到。
- View通过调用ViewModel提供的方法来与ViewModel交互

这里个人理解也不透彻，针对上述，有个想法：若是标准的写法使用DataBinding库是不是应该是这样的？？？ ->

（1）建立个BindingModel类，和普通的model类差不多，只是实现了DataBinding的数据可观察的方法

（2）ViewModel中处理事件，update 数据，然后需要更新UI时把model数据更新即可


可能是经历太少，ViewModel+监听的方式见过几个，我们项目也是这样。这里先插个眼，，，以后有新感悟了再回来整理~


MVVM与MVP的主要区别在于:

你不用去主动去刷新UI了，只要Model数据变了，会自动反映到UI上。换句话说，MVVM更像是自动化的MVP

很好理解，DataBinding的数据双向绑定避免了主动发数据触发接口回调，或者说使用DataBinding这些工作我们不必写了。


MVVM缺点：

- MVVM与MVP的主要区别在于双向数据绑定，但由于很多人(比如我)并不喜欢使用DataBinding，其实并没有使用MVVM双向绑定的特性，而是单一数据源
- 当页面复杂时，需要定义很多State，并且需要定义可变与不可变两种，状态会以双倍的速度膨胀，模板代码较多且容易遗忘
- View与ViewModel通过ViewModel暴露的方法交互，比较零乱难以维护


