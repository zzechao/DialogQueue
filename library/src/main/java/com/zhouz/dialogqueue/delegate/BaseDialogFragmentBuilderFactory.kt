package com.zhouz.dialogqueue.delegate

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.IPartInterceptor
import kotlin.reflect.KClass

abstract class BaseDialogFragmentBuilderFactory : IBuildFactory<Fragment>,
    DefaultLifecycleObserver {
    override var mDialog: Fragment? = null

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