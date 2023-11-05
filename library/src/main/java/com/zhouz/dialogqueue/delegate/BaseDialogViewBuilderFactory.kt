package com.zhouz.dialogqueue.delegate

import android.view.View
import androidx.core.view.doOnDetach
import com.zhouz.dialogqueue.IBuildFactory

abstract class BaseDialogViewBuilderFactory : IBuildFactory<View> {
    override var mDialog: View? = null

    override var mDialogDismissListener: (() -> Unit)? = null
    override fun attachDialogDismiss() {
        mDialog?.doOnDetach {
            mDialogDismissListener?.invoke()
        }
    }
}