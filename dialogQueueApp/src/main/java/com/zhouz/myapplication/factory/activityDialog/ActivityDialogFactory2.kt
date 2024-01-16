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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.reflect.KClass

class ActivityDialogFactory2 : BaseDialogActivityBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): ComponentActivity {
        return withTimeout(2000L) {
            suspendCancellableCoroutine {
                val callbacks = object : DefaultActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        it.resume(activity as ComponentActivity)
                        activity.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
                activity.application.registerActivityLifecycleCallbacks(callbacks)
                val intent = Intent(activity, ActivityDialog::class.java)
                activity.startActivity(intent)
                it.invokeOnCancellation {
                    activity.application.unregisterActivityLifecycleCallbacks(callbacks)
                }
            }
        }
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