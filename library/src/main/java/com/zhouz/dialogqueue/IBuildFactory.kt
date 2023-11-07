package com.zhouz.dialogqueue

import android.app.Activity
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
    var priority: Int

    /**
     * dialogId
     */
    val dialogId: Int

    /**
     * dismiss的监听
     */
    var mDialogDismissListener: (() -> Unit)?

    /**
     * 绑定的activity
     */
    val bindActivity: Array<KClass<out Activity>>

    /**
     * 是否保活
     */
    val isKeepALive: Boolean

    /**
     * 数据处理或者拦截器
     */
    val dialogPartInterceptors: Array<out IPartInterceptor>

    /**
     * 构建对应的dialog信息
     */
    suspend fun buildDialog(activity: Activity): T

    /**
     * 构建不同类似的dialog的消失方法
     */
    fun attachDialogDismiss()
}