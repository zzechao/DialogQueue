package com.zhouz.dialogqueue

import android.app.Activity
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

/**
 * @author:zhouz
 * @date: 2/24/21
 * 弹窗队列构造器
 */
typealias DialogDismissListener = (() -> Unit)

interface IBuildFactory<T> {

    /**
     * 构建的实体dialog
     */
    var mDialog: T?

    /**
     * 弹窗ID
     */
    val dialogID: Int

    /**
     * 透传字段
     */
    var extra: String

    /**
     * 优先级
     */
    fun priority(): Int {
        return 1
    }

    /**
     * dismiss的监听
     */
    val mDialogDismissListeners: CopyOnWriteArrayList<WeakReference<DialogDismissListener>>

    /**
     * 绑定的activity
     */
    fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf()
    }

    /**
     * 绑定的fragment
     */
    fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf()
    }

    /**
     * 是否保活
     */
    fun isKeepALive(): Boolean {
        return false
    }

    /**
     * 数据处理或者拦截器
     */
    fun dialogPartInterceptors(): Array<out IPartInterceptor> {
        return arrayOf()
    }

    /**
     * 构建对应的dialog信息
     */
    suspend fun buildDialog(activity: Activity, extra: String): T

    /**
     * 构建不同类似的dialog的消失方法
     */
    suspend fun attachDialogDismiss(): Boolean

    /**
     * 设置dismiss监听
     */
    fun addOnDismissListener(listener: DialogDismissListener)
}