package com.zhouz.dialogqueue.delegate

import android.view.View
import androidx.core.view.doOnDetach
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import java.lang.ref.WeakReference

abstract class BaseDialogViewBuilderFactory : IBuildFactory<View> {
    override var mDialog: View? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: MutableSet<WeakReference<DialogDismissListener>> = mutableSetOf()

    override fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        mDialog?.doOnDetach {
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