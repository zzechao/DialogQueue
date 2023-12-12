package com.zhouz.dialogqueue.delegate

import android.app.Dialog
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import java.lang.ref.WeakReference

abstract class BaseDialogCustomBuilderFactory : IBuildFactory<Dialog> {

    protected val logger = LoggerFactory.getLogger("BaseDialogCustomBuilderFactory")

    override var mDialog: Dialog? = null

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> = mutableSetOf()

    override fun attachDialogDismiss() {
        if (mDialog == null) throw IllegalStateException("please set mDialog value")
        logger.i("attachDialogDismiss mDialog:$mDialog")
        mDialog?.setOnDismissListener {
            mDialogDismissListeners.forEach {
                it.get()?.invoke()
            }
        }
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}