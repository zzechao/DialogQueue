package com.zhouz.dialogqueue

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 *
 */
interface IBuildFactory<T> {

    /**
     * 构建的实体dialog
     */
    var mDialog: T?

    /**
     * 优先级
     */
    fun priority(): Int {
        return 1
    }

    /**
     * dialogId
     */
    fun dialogId(): Int {
        return DialogQueueActivityDeal.getDialogId()
    }

    /**
     * dismiss的监听
     */
    var mDialogDismissListener: (() -> Unit)?

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
    suspend fun buildDialog(activity: Activity): T

    /**
     * 构建不同类似的dialog的消失方法
     */
    fun attachDialogDismiss()
}