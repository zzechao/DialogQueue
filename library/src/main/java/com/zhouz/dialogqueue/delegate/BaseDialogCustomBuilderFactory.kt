package com.zhouz.dialogqueue.delegate

import android.app.Dialog
import com.zhouz.dialogqueue.IBuildFactory

abstract class BaseDialogCustomBuilderFactory : IBuildFactory<Dialog> {

    override var mDialog: Dialog? = null

    override var mDialogDismissListener: (() -> Unit)? = null
    override fun attachDialogDismiss() {
        mDialog?.setOnDismissListener {
            mDialogDismissListener?.invoke()
        }
    }
}