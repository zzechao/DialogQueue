package com.zhouz.dialogqueue.delegate

import android.view.View
import androidx.core.view.doOnDetach
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.IBuildFactory
import java.lang.ref.WeakReference

abstract class BaseDialogViewBuilderFactory : IBuildFactory<View> {
    override var mDialog: View? = null

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> = mutableSetOf()

    override fun attachDialogDismiss() {
        if (mDialog == null) throw IllegalStateException("please set mDialog value")
        mDialog?.doOnDetach {
            mDialogDismissListeners.forEach {
                it.get()?.invoke()
            }
        }
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}