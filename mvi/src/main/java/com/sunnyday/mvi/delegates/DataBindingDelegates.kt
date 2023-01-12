package com.sunnyday.mvi.delegates

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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