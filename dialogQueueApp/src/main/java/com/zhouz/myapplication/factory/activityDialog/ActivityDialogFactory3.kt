package com.zhouz.myapplication.factory.activityDialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.zhouz.dialogqueue.DefaultActivityLifecycleCallbacks
import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.myapplication.MainActivity
import com.zhouz.myapplication.SecondActivity
import com.zhouz.myapplication.dialog.ActivityDialog
import com.zhouz.myapplication.fragment.FirstFragment
import com.zhouz.myapplication.fragment.SecondFragment
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.reflect.KClass

private var index = 0

class ActivityDialogFactory3 : BaseDialogActivityBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): ComponentActivity {
        return withTimeout(2000L) {
            suspendCancellableCoroutine {
                val callbacks = object : DefaultActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        it.resume(activity as ComponentActivity)
                        activity.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
                val content = "测试 ActivityDialogFactory ${index + 1}"
                index += 1
                activity.application.registerActivityLifecycleCallbacks(callbacks)
                val intent = Intent(activity, ActivityDialog::class.java)
                intent.putExtra("content", content)
                activity.startActivity(intent)
                it.invokeOnCancellation {
                    activity.application.unregisterActivityLifecycleCallbacks(callbacks)
                }
            }
        }
    }

    /**
     * 绑定MainActivity
     */
    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(SecondActivity::class)
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