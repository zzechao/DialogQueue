package com.zhouz.dialogqueue

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zhouz.dialogqueue.DialogEx.log
import com.zhouz.dialogqueue.weak.weak
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

object DialogQueueActivityDeal : FragmentManager.FragmentLifecycleCallbacks(),
    DefaultActivityLifecycleCallbacks {

    private val dialogIdAtomic = AtomicInteger(0)
    private var mWeakReferenceActivity by weak<Activity>()

    private val queue =
        PriorityBlockingQueue<IBuildFactory<out Any>>(20, Comparator { dialog1, dialog2 ->
            val diff = dialog2.priority() - dialog1.priority()
            return@Comparator if (diff == 0) {
                val diff2 = dialog1.dialogId() - dialog2.dialogId()
                diff2 / abs(diff2)
            } else {
                diff / abs(diff)
            }
        })

    /**
     * 不是对应Activity的弹窗
     */
    private val deActivityList = mutableListOf<IBuildFactory<out Any>>()

    private val deFragmentList = mutableListOf<IBuildFactory<out Any>>()

    fun getDialogId(): Int {
        return dialogIdAtomic.incrementAndGet()
    }

    fun addDialogBuilder(factory: IBuildFactory<out Any>) {
        queue.offer(factory)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mWeakReferenceActivity = activity
        queue.removeAll { !it.isKeepALive() }
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        (activity as? FragmentActivity)?.watchFragment()
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        deActivityList.forEach {
            queue.offer(it)
        }
        deActivityList.clear()
        //(activity as? FragmentActivity)?.showQueueFragment()
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        (activity as? FragmentActivity)?.deWatchFragment()
    }

    override fun onActivityDestroyed(activity: Activity) {
        mWeakReferenceActivity = null
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        f.watchFragment()
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        f.deWatchFragment()
    }

    private fun FragmentActivity.watchFragment() {
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            this@DialogQueueActivityDeal,
            false
        )
    }

    private fun Fragment.watchFragment() {
        childFragmentManager.registerFragmentLifecycleCallbacks(this@DialogQueueActivityDeal, false)
    }

    private fun FragmentActivity.deWatchFragment() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(this@DialogQueueActivityDeal)
    }

    private fun Fragment.deWatchFragment() {
        childFragmentManager.unregisterFragmentLifecycleCallbacks(this@DialogQueueActivityDeal)
    }

    /**
     * 执行弹窗队列
     */
}