package com.zhouz.dialogqueue.delegate

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import java.lang.ref.WeakReference

abstract class BaseDialogActivityBuilderFactory : IBuildFactory<ComponentActivity>, DefaultLifecycleObserver {
    override var mDialog: ComponentActivity? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> = mutableSetOf()

    override fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        mDialog?.lifecycle?.addObserver(this)
        return true
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