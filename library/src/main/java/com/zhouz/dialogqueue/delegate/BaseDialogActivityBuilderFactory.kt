package com.zhouz.dialogqueue.delegate

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseDialogActivityBuilderFactory : IBuildFactory<ComponentActivity>, DefaultLifecycleObserver {
    override var mDialog: ComponentActivity? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: CopyOnWriteArrayList<WeakReference<DialogDismissListener>> = CopyOnWriteArrayList()

    override suspend fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        withContext(Dispatchers.Main) {
            mDialog?.lifecycle?.addObserver(this@BaseDialogActivityBuilderFactory)
        }
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