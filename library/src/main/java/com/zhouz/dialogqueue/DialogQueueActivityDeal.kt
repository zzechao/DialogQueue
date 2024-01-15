package com.zhouz.dialogqueue

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.dialogqueue.weak.weak
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

/**
 * @author:zhouz
 * @date: 2/24/21
 * 弹窗队列处理类
 */
object DialogQueueActivityDeal : FragmentManager.FragmentLifecycleCallbacks(),
    DefaultActivityLifecycleCallbacks {


    private val logger = LoggerFactory.getLogger("DialogQueueActivityDeal")

    private val dialogIdAtomic = AtomicInteger(0)
    private var mWeakReferenceActivity by weak<Activity>()
    private var mWeakReferenceFragment by weak<Fragment>()
    private var job: Job? = null

    private var loggingExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        logger.e("CoroutineException", "Coroutine exception occurred. $context", throwable)
    }

    private val dispatcher = ThreadPoolExecutor(
        1,
        1,
        0,
        TimeUnit.MILLISECONDS,
        LinkedBlockingDeque(),
        object : ThreadFactory {
            private val mThreadId = AtomicInteger(0)

            override fun newThread(r: Runnable): Thread {
                val t = Thread(r)
                t.name = String.format("dialog_thread_%d", mThreadId.getAndIncrement())
                return t
            }
        }).asCoroutineDispatcher()

    val dialogQueueScope = CoroutineScope(SupervisorJob() + dispatcher + loggingExceptionHandler)

    private val isShow = AtomicBoolean(false)
    private val isRunQueue = AtomicBoolean(false)

    private val queue =
        PriorityBlockingQueue<IBuildFactory<out Any>>(20, Comparator { dialog1, dialog2 ->
            val diff = dialog2.priority() - dialog1.priority()
            return@Comparator if (diff == 0) {
                val diff2 = dialog1.dialogID - dialog2.dialogID
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
        logger.i("addDialogBuilder extra:${factory.extra} id:${factory.dialogID}")
        queue.offer(factory)
        showQueueDialog()
    }


    /**
     * ==============activity=======================
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logger.i("onActivityCreated activity:${activity::class.java}")
        queue.removeAll { !it.isKeepALive() }
        (activity as? FragmentActivity)?.watchFragment()
    }

    override fun onActivityStarted(activity: Activity) {
        logger.i("onActivityStarted activity:${activity::class.java}")
        super.onActivityStarted(activity)
        mWeakReferenceActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        logger.i("onActivityResumed activity:${activity::class.java}")
        super.onActivityResumed(activity)
        deActivityList.firstOrNull { it.bindActivity().contains(activity::class) }?.let {
            job?.cancel()
            deFragmentList.forEach {
                queue.offer(it)
            }
            deFragmentList.clear()

            deActivityList.forEach {
                queue.offer(it)
            }
            deActivityList.clear()
        }
        showQueueDialog()
    }

    override fun onActivityStopped(activity: Activity) {
        logger.i("onActivityStopped activity:${activity::class.java}")
        super.onActivityStopped(activity)
        if (mWeakReferenceActivity == activity) {
            mWeakReferenceActivity = null
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        logger.i("onActivityDestroyed activity:${activity::class.java}")
        job?.cancel()
        (activity as? FragmentActivity)?.deWatchFragment()
    }

    /**
     * =================fragment ====================
     */
    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        logger.i("onFragmentCreated Fragment:${f::class.java}")
        super.onFragmentCreated(fm, f, savedInstanceState)
        f.watchFragment()
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        logger.i("onFragmentStarted Fragment:${f::class.java}")
        super.onFragmentStarted(fm, f)
        mWeakReferenceFragment = f
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        logger.i("onFragmentResumed Fragment:${f::class.java}")
        super.onFragmentResumed(fm, f)
        deFragmentList.firstOrNull { it.bindFragment().contains(f::class) }?.let {
            job?.cancel()
            deFragmentList.forEach {
                queue.offer(it)
            }
            deFragmentList.clear()
        }
        showQueueDialog()
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        logger.i("onFragmentStopped Fragment:${f::class.java}")
        super.onFragmentStopped(fm, f)
        if (mWeakReferenceFragment == f) {
            mWeakReferenceFragment = null
        }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        logger.i("onFragmentDestroyed Fragment:${f::class.java}")
        super.onFragmentDestroyed(fm, f)
        f.deWatchFragment()
    }


    /**
     * ================watch fragment =================
     */
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
    private fun showQueueDialog() {
        if (queue.isEmpty()) return
        logger.i("showQueueDialog size:${queue.size}-${isRunQueue.get()}-${isShow.get()}")
        if (isRunQueue.get()) return
        if (isShow.get()) return
        isRunQueue.compareAndSet(false, true)
        logger.i("showQueueDialog job?.cancel()")
        job?.cancel()
        job = dialogQueueScope.launch {
            var data = queue.poll()
            apply {
                while (data != null) {
                    val intercept = data.dialogPartInterceptors().firstOrNull { it.intercept() }
                    if (intercept == null) {
                        val activityClazz = mWeakReferenceActivity?.javaClass?.kotlin
                        val fragmentClazz = mWeakReferenceFragment?.javaClass?.kotlin
                        logger.i(
                            "activityClazz:$activityClazz fragmentClazz:$fragmentClazz " +
                                    "data.bindActivity():${data.bindActivity().firstOrNull()} " +
                                    "data.bindFragment():${data.bindFragment().firstOrNull()}" +
                                    "dataClazz:${data::class.java}"
                        )
                        when {
                            data.bindActivity().isEmpty() && data.bindFragment().isEmpty() -> {
                                logger.i("action 1 showDialog")
                                mWeakReferenceActivity?.let {
                                    data.showDialog(it)
                                    return@apply
                                }
                            }

                            fragmentClazz != null && data.bindFragment().contains(fragmentClazz) &&
                                    activityClazz != null && data.bindActivity()
                                .contains(activityClazz) -> {
                                mWeakReferenceFragment?.activity?.javaClass?.kotlin?.let {
                                    if (data.bindActivity().contains(it)) {
                                        logger.i("action 2 showDialog")
                                        mWeakReferenceActivity?.let {
                                            data.showDialog(it)
                                            return@apply
                                        }
                                    }
                                }
                                logger.i("action 2 queue.poll()")
                                data?.let { deFragmentList.add(it) }
                                data = queue.poll()
                            }

                            activityClazz != null && data.bindActivity()
                                .contains(activityClazz) -> {
                                if (data.bindFragment().isNotEmpty()) {
                                    logger.i("action 3 queue.poll()")
                                    data?.let { deFragmentList.add(it) }
                                    data = queue.poll()
                                } else {
                                    logger.i("action 3 showDialog")
                                    mWeakReferenceActivity?.let {
                                        data.showDialog(it)
                                        return@apply
                                    }
                                }
                            }

                            fragmentClazz != null && data.bindFragment()
                                .contains(fragmentClazz) -> {
                                if (data.bindActivity().isNotEmpty()) {
                                    logger.i("action 4 queue.poll()")
                                    if (data?.isKeepALive() == true) {
                                        data?.let { deActivityList.add(it) }
                                    }
                                    data = queue.poll()
                                } else {
                                    logger.i("action 4 showDialog")
                                    mWeakReferenceActivity?.let {
                                        data.showDialog(it)
                                        return@apply
                                    }
                                }
                            }

                            data.bindActivity().isNotEmpty() -> {
                                logger.i("action 5 queue.poll()")
                                if (data?.isKeepALive() == true) {
                                    data?.let { deActivityList.add(it) }
                                }
                                data = queue.poll()
                            }

                            data.bindFragment().isNotEmpty() -> {
                                logger.i("action 6 queue.poll()")
                                data?.let { deFragmentList.add(it) }
                                data = queue.poll()
                            }

                            else -> {
                                logger.i("action 7 queue.poll()")
                                data = queue.poll()
                            }
                        }
                    }
                }
            }
            isRunQueue.compareAndSet(true, false)
            logger.i("showQueueDialog end")
        }
    }

    /**
     * 展示弹窗
     */
    private suspend fun <T> IBuildFactory<T>.showDialog(activity: Activity) {
        if (isShow.compareAndSet(false, true)) {
            addOnDismissListener {
                logger.i("showQueueDialog addOnDismissListener")
                if (isShow.compareAndSet(true, false)) {
                    showQueueDialog()
                }
            }
            logger.i("showQueueDialog factory:$this")
            val dialog = withContext(Dispatchers.Main) {
                logger.i("showQueueDialog buildDialog")
                buildDialog(activity, this@showDialog.extra)
            }
            logger.i("showQueueDialog attachDialogDismiss dialog:$dialog")
            this.mDialog = dialog
            if (!attachDialogDismiss()) {
                if (isShow.compareAndSet(true, false)) {
                    showQueueDialog()
                }
            }
        }
    }

    /**
     * 去除队列的dialogID
     */
    fun removeFloatDialog(dialogID: Int) {
        queue.find { it.dialogID == dialogID }?.let {
            queue.remove(it)
            return
        }
        deActivityList.find { it.dialogID == dialogID }?.let {
            deActivityList.remove(it)
            return
        }
        deFragmentList.find { it.dialogID == dialogID }?.let {
            deFragmentList.remove(it)
            return
        }
    }
}