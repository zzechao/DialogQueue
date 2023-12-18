package com.zhouz.myapplication.factory.fragmentDialog

import android.app.Activity
import androidx.fragment.app.DialogFragment
import com.zhouz.dialogqueue.delegate.BaseDialogFragmentBuilderFactory
import com.zhouz.myapplication.dialog.FragmentDialog


/**
 * @author:zhouz
 * @date: 2023/12/18 10:30
 * description：创建dialogFragment弹窗的factory，绑定了特定的MainActivity和FirstFragment，isKeepALive设置包活
 */
class FragmentDialogFactory : BaseDialogFragmentBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): DialogFragment {
        return FragmentDialog()
    }
}