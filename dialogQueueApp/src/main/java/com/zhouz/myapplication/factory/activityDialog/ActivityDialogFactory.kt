package com.zhouz.myapplication.factory.activityDialog

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.dialogqueue.startReturnActivity
import com.zhouz.myapplication.MainActivity
import com.zhouz.myapplication.dialog.ActivityDialog
import com.zhouz.myapplication.fragment.FirstFragment
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/1/15 19:01
 * description：创建指定场景的factory
 */
private var index = 0

class ActivityDialogFactory : BaseDialogActivityBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): ComponentActivity? {
        val content = "测试 ActivityDialogFactory ${index + 1}"
        index += 1
        val bundle = Bundle()
        bundle.putString("content", content)
        return activity.startReturnActivity(ActivityDialog::class.java, bundle)
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