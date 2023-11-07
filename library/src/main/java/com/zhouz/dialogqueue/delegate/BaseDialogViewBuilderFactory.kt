package com.zhouz.dialogqueue.delegate

import android.app.Activity
import android.view.View
import androidx.core.view.doOnDetach
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.IPartInterceptor
import kotlin.reflect.KClass

abstract class BaseDialogViewBuilderFactory : IBuildFactory<View> {
    override var mDialog: View? = null

    override var priority: Int = 1

    override val dialogId: Int = DialogQueueActivityDeal.getDialogId()

    override var mDialogDismissListener: (() -> Unit)? = null

    override val bindActivity: Array<KClass<out Activity>> = arrayOf()

    override val isKeepALive: Boolean = false

    override val dialogPartInterceptors: Array<out IPartInterceptor> = arrayOf()
    override fun attachDialogDismiss() {
        mDialog?.doOnDetach {
            mDialogDismissListener?.invoke()
        }
    }
}