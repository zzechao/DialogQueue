package com.zhouz.dialogqueue

import android.app.Activity

/**
 *
 */
interface IBuildFactory<T> {

    /**
     * 构建的实体dialog
     */
    var mDialog: T?

    /**
     * dismiss的监听
     */
    var mDialogDismissListener: (() -> Unit)?

    /**
     * 构建对应的dialog信息
     */
    suspend fun buildDialog(activity: Activity): T

    /**
     * 构建不同类似的dialog的消失方法
     */
    fun attachDialogDismiss()
}