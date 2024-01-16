package com.zhouz.myapplication.factory.fragmentDialog

import android.app.Activity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zhouz.dialogqueue.delegate.BaseDialogFragmentBuilderFactory
import com.zhouz.myapplication.MainActivity
import com.zhouz.myapplication.dialog.FragmentDialog
import com.zhouz.myapplication.fragment.FirstFragment
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2023/12/18 10:30
 * description：创建dialogFragment弹窗的factory，绑定了特定的MainActivity和FirstFragment，isKeepALive设置包活
 */
private var index = 0

class FragmentDialogFactory : BaseDialogFragmentBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): DialogFragment {
        val content = "测试 FragmentDialogFactory ${index + 1}"
        index += 1
        val fragmentDialog = FragmentDialog.newInstance(extra, content)
        fragmentDialog.show((activity as FragmentActivity).supportFragmentManager, "FragmentDialog")
        return fragmentDialog
    }

    /**
     * 绑定MainActivity
     */
    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(MainActivity::class)
    }

    /**
     * 绑定FirstFragment
     */
    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(FirstFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}