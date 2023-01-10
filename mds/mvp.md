# mvp

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvp.png)

###### 1、介绍

MVP架构主要分为以下几个部分：

- Model：还是原来的模型层，数据，相对mvc这一层没啥变化。
- View：对应Activity与xml，其职责是负责UI展示、与Presenter进行交互。这一层与model层无耦合。
- Presenter：持有view与model的引用、负责业务逻辑的处理。

###### 2、实践

（1）model

```kotlin
/**
 * Create by SunnyDay /01/10 10:17:37
 * mvp中model还是原来的model。
 * 为了方便，这里可假定数据就是服务器存的User信息。
 */
data class User(val account: String = "admin",  val password: String = "123456")
```

（2）view

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".view.MvpLoginActivity">

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

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin

class MvpLoginActivity : AppCompatActivity(), BaseView {
    private lateinit var loginPresenter: LoginPresenterImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvp_login)
        //持有Presenter引用
        loginPresenter = LoginPresenterImpl().apply {
            //Presenter持有view
            attachView(this@MvpLoginActivity)
        }
        login.setOnClickListener {
            val inputAccount = userAccount.text.toString()
            val inputPassword = userPassWord.text.toString()
            val severUserInfo = User()
            // 通过Presenter处理业务。（Presenter持有model数据）
            loginPresenter.login(inputAccount, inputPassword, severUserInfo)
        }
    }

    /**
    * View的更新，通过回调的方式刷新。
    * */
    override fun loginSuccess(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun loginFailure(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.detachView()
    }
}
```

（3）presenter

```kotlin
/**
 * Create by SunnyDay /01/10 10:26:34
 */
class LoginPresenterImpl : BasePresenter<BaseView> {
    private var mView: BaseView? = null
    override fun attachView(view: BaseView) {
        this.mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun login(inputAccount:String,inputPassword:String,user: User) {
        mView?.let {
            if (user.account == inputAccount && user.password == inputPassword) {
                it.loginSuccess("login success")
            } else {
                it.loginFailure("login failure")
            }
        }
    }
}
```

（4）补充

期间我们抽取了接口，方便复用。

```kotlin
/**
 * Create by SunnyDay /01/10 10:24:39
 */
interface BasePresenter<V> {
    fun attachView(view: V)
    fun detachView()
    fun login(inputAccount:String,inputPassword:String,user: User)
}
```

```kotlin
/**
 * Create by SunnyDay /01/10 10:22:34
 */
interface BaseView {
    fun loginSuccess(msg: String)
    fun loginFailure(msg: String)
}
```

###### 3、总结

（1）核心实现

- 把Activity中的UI逻辑抽象成view接口
- 把业务逻辑抽象成presenter接口
- model类还是原来的model类

（2）好处

- Activity只处理生命周期相关任务，代码简洁。
- View不直接与Model进行交互，降低耦合。

（3）缺点

- Presenter层通过接口与View通信，实际上持有了View的引用
- 但是随着业务逻辑的增加，一个页面可能会非常复杂，这样就会造成View的接口会很庞大。


