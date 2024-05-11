package com.zhouz.dialogqueue.delegate

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseDialogActivityBuilderFactory : IBuildFactory<ComponentActivity>, LifecycleEventObserver {

    open val logger = LoggerFactory.getLogger("BaseDialogActivityBuilderFactory")

    override var mDialog: ComponentActivity? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: CopyOnWriteArrayList<WeakReference<DialogDismissListener>> = CopyOnWriteArrayList()

    override var priority: Int = 1

    override suspend fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        logger.i("attachDialogDismiss mDialog:$mDialog")
        withContext(Dispatchers.Main) {
            mDialog?.lifecycle?.addObserver(this@BaseDialogActivityBuilderFactory)
        }
        return true
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        logger.i("onStateChanged source:$source mDialogDismissListeners:${mDialogDismissListeners.size} mDialog:$mDialog event:$event")
        if (event == Lifecycle.Event.ON_DESTROY) {
            mDialogDismissListeners.forEach {
                it.get()?.invoke()
            }
        }
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}