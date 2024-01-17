package com.zhouz.myapplication.factory.activityDialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.zhouz.dialogqueue.DefaultActivityLifecycleCallbacks
import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.dialogqueue.startReturnActivity
import com.zhouz.myapplication.dialog.ActivityDialog
import com.zhouz.myapplication.fragment.SecondFragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/1/15 19:01
 * description：创建指定场景的factory
 */
private var index = 0

class ActivityDialogFactory4 : BaseDialogActivityBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): ComponentActivity? {
        val content = "测试 ActivityDialogFactory4 ${index + 1}"
        index += 1
        val bundle = Bundle()
        bundle.putString("content", content)
        return activity.startReturnActivity(ActivityDialog::class.java, bundle)
    }

    /**
     * 绑定SecondFragment
     */
    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(SecondFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}