package com.zhouz.dialogqueue.delegate

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

abstract class BaseDialogFragmentBuilderFactory : IBuildFactory<DialogFragment>,
    DefaultLifecycleObserver {
    open val logger = LoggerFactory.getLogger("BaseDialogFragmentBuilderFactory")

    override var mDialog: DialogFragment? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> = mutableSetOf()

    override suspend fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        logger.i("attachDialogDismiss mDialog:$mDialog")
        withContext(Dispatchers.Main) {
            mDialog?.lifecycle?.addObserver(this@BaseDialogFragmentBuilderFactory)
        }
        return true
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        logger.i("onDestroy mDialogDismissListeners:${mDialogDismissListeners.size}")
        mDialogDismissListeners.forEach {
            it.get()?.invoke()
        }
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}