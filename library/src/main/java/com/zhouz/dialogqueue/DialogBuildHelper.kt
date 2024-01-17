package com.zhouz.dialogqueue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume


/**
 * @author:zhouz
 * @date: 2024/1/17 15:22
 * description：弹窗公共方法
 */

/**
 * 启动对应的activity 的dialog，并返回activity对象
 */
suspend inline fun Activity.startReturnActivity(cls: Class<*>, bundle: Bundle, timeOut: Long = 2000L): ComponentActivity? {
    return withContext(Dispatchers.Main) {
        withTimeoutOrNull(timeOut) {
            suspendCancellableCoroutine {
                val callbacks = object : DefaultActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        it.resume(activity as ComponentActivity)
                        activity.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
                val intent = Intent(this@startReturnActivity, cls)
                intent.putExtras(bundle)
                this@startReturnActivity.startActivity(intent)
                it.invokeOnCancellation {
                    this@startReturnActivity.application.unregisterActivityLifecycleCallbacks(callbacks)
                }
            }
        }
    }
}

inline fun View.safeDoOnAttach(crossinline action: (view: View) -> Unit): View.OnAttachStateChangeListener? {
    var listener: View.OnAttachStateChangeListener? = null
    if (ViewCompat.isAttachedToWindow(this)) {
        action(this)
    } else {
        listener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                action(view)
            }

            override fun onViewDetachedFromWindow(view: View) {}
        }
        addOnAttachStateChangeListener(listener)
    }
    return listener
}

inline fun View.safeDoOnDetach(crossinline action: (view: View) -> Unit): View.OnAttachStateChangeListener? {
    var listener: View.OnAttachStateChangeListener? = null
    if (!ViewCompat.isAttachedToWindow(this)) {
        action(this)
    } else {
        listener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {}

            override fun onViewDetachedFromWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                action(view)
            }
        }
        addOnAttachStateChangeListener(listener)
    }
    return listener
}