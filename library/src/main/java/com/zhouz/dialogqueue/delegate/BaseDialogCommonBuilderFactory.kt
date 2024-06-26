package com.zhouz.dialogqueue.delegate

import android.app.Dialog
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.checkWithDispatchersMain
import com.zhouz.dialogqueue.log.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseDialogCommonBuilderFactory : IBuildFactory<Dialog> {

    open val logger = LoggerFactory.getLogger("BaseDialogCustomBuilderFactory")

    override var mDialog: Dialog? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override var priority: Int = 1

    override val mDialogDismissListeners: CopyOnWriteArrayList<WeakReference<DialogDismissListener>> =
        CopyOnWriteArrayList()

    override suspend fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        logger.i("attachDialogDismiss mDialog:$mDialog")
        checkWithDispatchersMain {
            mDialog?.setOnDismissListener {
                mDialogDismissListeners.forEach {
                    it.get()?.invoke()
                }
            }
        }
        return true
    }

    override fun addOnDismissListener(listener: DialogDismissListener) {
        mDialogDismissListeners.add(WeakReference(listener))
    }
}