package com.zhouz.dialogqueue.delegate

import android.app.Dialog
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import java.lang.ref.WeakReference

abstract class BaseDialogCustomBuilderFactory : IBuildFactory<Dialog> {

    open val logger = LoggerFactory.getLogger("BaseDialogCustomBuilderFactory")

    override var mDialog: Dialog? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> =
        mutableSetOf()

    override fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        logger.i("attachDialogDismiss mDialog:$mDialog")
        mDialog?.setOnDismissListener {
            mDialogDismissListeners.forEach {
                it.get()?.invoke()
            }
        }
        return true
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}