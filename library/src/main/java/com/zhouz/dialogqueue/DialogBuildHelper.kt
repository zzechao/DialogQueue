package com.zhouz.dialogqueue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import com.zhouz.dialogqueue.log.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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
                        if (cls.simpleName == activity::class.simpleName) {
                            it.resume(activity as ComponentActivity)
                            this@startReturnActivity.application.unregisterActivityLifecycleCallbacks(this)
                        }
                    }
                }
                this@startReturnActivity.application.registerActivityLifecycleCallbacks(callbacks)
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

/**
 * 上层构建 activity file的创建 ， 并挂起等待绑定
 */
suspend inline fun Activity.startReturnActivity(cls: Class<*>, timeOut: Long = 2000L, crossinline builder: () -> Unit): ComponentActivity? {
    return withContext(Dispatchers.Main) {
        withTimeoutOrNull(timeOut) {
            suspendCancellableCoroutine {
                val callbacks = object : DefaultActivityLifecycleCallbacks {
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        LoggerFactory.getLogger("DialogQueueActivityDeal").i("startReturnActivity cls:$cls activity:$activity")
                        if (cls.simpleName == activity::class.simpleName) {
                            LoggerFactory.getLogger("DialogQueueActivityDeal").i("startReturnActivity cls:$cls resume")
                            it.resume(activity as ComponentActivity)
                            this@startReturnActivity.application.unregisterActivityLifecycleCallbacks(this)
                        }
                    }
                }
                this@startReturnActivity.application.registerActivityLifecycleCallbacks(callbacks)
                builder.invoke()
                it.invokeOnCancellation {
                    LoggerFactory.getLogger("DialogQueueActivityDeal").i("invokeOnCancellation")
                    this@startReturnActivity.application.unregisterActivityLifecycleCallbacks(callbacks)
                }
            }
        }
    }
}

/**
 * 挂起等待view的attachWindow
 */
suspend inline fun View.viewAttachWindowAwait(timeOut: Long = 2000L): View? {
    return withContext(Dispatchers.Main) {
        withTimeout(timeOut) {
            suspendCancellableCoroutine { con ->
                val listener = this@viewAttachWindowAwait.safeDoOnAttach {
                    con.resume(it)
                }
                con.invokeOnCancellation {
                    this@viewAttachWindowAwait.removeOnAttachStateChangeListener(listener)
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