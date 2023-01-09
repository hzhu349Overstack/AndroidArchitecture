# mvc

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvc.png)

###### 1、介绍

mvc是安卓默认的架构，该架构主要分为如下几部分：

- view：视图层，对应Android中的xml布局或者java代码view
- controller：控制层，主要负责业务逻辑，在android中由activity承担，但activity还负责了视图的显示因此导致职责过重。
- model：数据，数据的来源一般是网络、数据库等。

###### 2、实践

（1）model

```kotlin
/**
 * Create by SunnyDay /01/09 14:57:37
 * model 代表具体的数据，一般model来源自网络、数据库等，这里为了方便进行省略直接给了一个默认数据。
 */
data class User(val account: String = "admin",  val password: String = "123456")
```

（2）view/controller

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
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

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //负责view的展示（充当了View的功能）
        setContentView(R.layout.activity_login)
        //负责业务逻辑的处理（充当了controller的功能）
        val user = User() // 充当controller与model进行交互
        login.setOnClickListener { // 充当controller与view进行交互
            val account = userAccount.text.toString()
            val password = userPassWord.text.toString()
            if (account == user.account && password == user.password){
                Toast.makeText(applicationContext,"login success!",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(applicationContext,"login failure!",Toast.LENGTH_LONG).show()
            }
        }
    }
}
```

###### 3、总结

由于activity中xml布局功能被设计的太弱，activity实际是充当了view与controller两层的职责，所以android中的mvc更像下面的那种形式。

总结下mvc模式在android上的弊端：

- Activity同时负责View与Controller层的工作，职责过重。
- Model层与View层存在耦合，存在互相依赖。



