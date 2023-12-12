package com.zhouz.dialogqueue.delegate

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.IBuildFactory
import java.lang.ref.WeakReference

abstract class BaseDialogActivityBuilderFactory : IBuildFactory<ComponentActivity>, DefaultLifecycleObserver {
    override var mDialog: ComponentActivity? = null

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> = mutableSetOf()

    override fun attachDialogDismiss() {
        if (mDialog == null) throw IllegalStateException("please set mDialog value")
        mDialog?.lifecycle?.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mDialogDismissListeners.forEach {
            it.get()?.invoke()
        }
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}