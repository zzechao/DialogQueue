package com.zhouz.dialogqueue.delegate

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.IPartInterceptor
import kotlin.reflect.KClass

abstract class BaseDialogActivityBuilderFactory : IBuildFactory<ComponentActivity>,
    DefaultLifecycleObserver {
    override var mDialog: ComponentActivity? = null

    override var priority: Int = 1

    override val dialogId: Int = DialogQueueActivityDeal.getDialogId()

    override var mDialogDismissListener: (() -> Unit)? = null

    override val bindActivity: Array<KClass<out Activity>> = arrayOf()

    override val isKeepALive: Boolean = false

    override val dialogPartInterceptors: Array<out IPartInterceptor> = arrayOf()

    override fun attachDialogDismiss() {
        mDialog?.lifecycle?.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mDialogDismissListener?.invoke()
    }
}