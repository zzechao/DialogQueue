package com.zhouz.myapplication.factory.fragmentDialog

import android.app.Activity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.zhouz.dialogqueue.delegate.BaseDialogFragmentBuilderFactory
import com.zhouz.myapplication.SecondActivity
import com.zhouz.myapplication.dialog.FragmentDialog
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2023/12/18 10:30
 * description：创建dialogFragment弹窗的factory，只绑定了SecondActivity
 */
private var index = 0

class FragmentDialogFactory2 : BaseDialogFragmentBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): DialogFragment {
        val content = "测试 FragmentDialogFactory2 ${index + 1}"
        val fragmentDialog = FragmentDialog.newInstance(extra, content)
        fragmentDialog.show((activity as FragmentActivity).supportFragmentManager, "FragmentDialog")
        return fragmentDialog
    }

    /**
     * 绑定SecondActivity
     */
    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(SecondActivity::class)
    }

    /**
     *
     */
    override fun isKeepALive(): Boolean {
        return true
    }
}