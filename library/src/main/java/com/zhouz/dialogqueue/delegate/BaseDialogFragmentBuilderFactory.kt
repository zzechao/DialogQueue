package com.zhouz.dialogqueue.delegate

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zhouz.dialogqueue.IBuildFactory

abstract class BaseDialogFragmentBuilderFactory : IBuildFactory<Fragment>,
    DefaultLifecycleObserver {
    override var mDialog: Fragment? = null

    override var mDialogDismissListener: (() -> Unit)? = null
    override fun attachDialogDismiss() {
        mDialog?.lifecycle?.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mDialogDismissListener?.invoke()
    }
}