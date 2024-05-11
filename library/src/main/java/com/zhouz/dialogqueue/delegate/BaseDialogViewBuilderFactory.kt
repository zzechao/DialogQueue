package com.zhouz.dialogqueue.delegate

import android.view.View
import androidx.core.view.doOnDetach
import com.zhouz.dialogqueue.DialogDismissListener
import com.zhouz.dialogqueue.DialogQueueActivityDeal
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.dialogqueue.safeDoOnDetach
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

abstract class BaseDialogViewBuilderFactory : IBuildFactory<View> {

    open val logger = LoggerFactory.getLogger("BaseDialogViewBuilderFactory")

    override var mDialog: View? = null

    override var extra: String = ""

    override val dialogID: Int = DialogQueueActivityDeal.getDialogId()

    override val mDialogDismissListeners: CopyOnWriteArrayList<WeakReference<DialogDismissListener>> = CopyOnWriteArrayList()

    override var priority: Int = 1
    override suspend fun attachDialogDismiss(): Boolean {
        if (mDialog == null) return false
        logger.i("attachDialogDismiss mDialog:$mDialog")
        withContext(Dispatchers.Main) {
            mDialog?.safeDoOnDetach {
                logger.i("doOnDetach mDialogDismissListeners:${mDialogDismissListeners.size}")
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