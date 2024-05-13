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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
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
        PriorityBlockingQueue<IBuildFactory<*>>(20, Comparator { dialog1, dialog2 ->
            val diff = dialog2.priority - dialog1.priority
            return@Comparator if (diff == 0) {
                val diff2 = dialog1.dialogID - dialog2.dialogID
                logger.d("queue diff2:$diff2")
                if (diff2 == 0) {
                    0
                } else {
                    diff2 / abs(diff2)
                }
            } else {
                logger.d("queue diff:$diff")
                diff / abs(diff)
            }
        })

    /**
     * 不是对应Activity的弹窗
     */
    private val deActivityList = mutableListOf<IBuildFactory<*>>()

    private val deFragmentList = mutableListOf<IBuildFactory<*>>()

    fun getDialogId(): Int {
        return dialogIdAtomic.incrementAndGet()
    }

    fun addDialogBuilder(factory: IBuildFactory<*>) {
        logger.i("addDialogBuilder extra:${factory.extra} id:${factory.dialogID}")
        queue.offer(factory)
        showQueueDialog()
    }


    /**
     * ==============activity=======================
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logger.d("onActivityCreated activity:${activity::class.java}")
        if (activity !is IDialogQ) {
            queue.removeAll { !it.isKeepALive() }
            (activity as? FragmentActivity)?.watchFragment()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        logger.d("onActivityStarted activity:${activity::class.java}")
        super.onActivityStarted(activity)
        if (activity !is IDialogQ) {
            mWeakReferenceActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        logger.d("onActivityResumed activity:${activity::class.java}")
        super.onActivityResumed(activity)
        if (activity !is IDialogQ) {
            deActivityList.firstOrNull { it.bindActivity().contains(activity::class) }?.let {
                jobCancelAndReset {
                    deFragmentList.forEach {
                        queue.offer(it)
                    }
                    deFragmentList.clear()

                    deActivityList.forEach {
                        queue.offer(it)
                    }
                    deActivityList.clear()
                    showQueueDialog()
                }
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        logger.d("onActivityStopped activity:${activity::class.java}")
        super.onActivityStopped(activity)
        if (mWeakReferenceActivity == activity && activity !is IDialogQ) {
            mWeakReferenceActivity = null
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        logger.d("onActivityDestroyed activity:${activity::class.java}")
        if (activity !is IDialogQ) {
            if (mWeakReferenceActivity == activity) {
                jobCancelAndReset()
            }
            (activity as? FragmentActivity)?.deWatchFragment()
        }
    }

    /**
     * =================fragment ====================
     */
    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        logger.d("onFragmentCreated Fragment:${f::class.java}")
        super.onFragmentCreated(fm, f, savedInstanceState)
        if (f !is IDialogQ) {
            f.watchFragment()
        }
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        logger.d("onFragmentStarted Fragment:${f::class.java}")
        super.onFragmentStarted(fm, f)
        if (f !is IDialogQ) {
            mWeakReferenceFragment = f
        }
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        logger.d("onFragmentResumed Fragment:${f::class.java}")
        super.onFragmentResumed(fm, f)
        if (f !is IDialogQ) {
            if (isShow.get()) return
            deFragmentList.firstOrNull { it.bindFragment().contains(f::class) }?.let {
                jobCancelAndReset {
                    deFragmentList.forEach {
                        queue.offer(it)
                    }
                    deFragmentList.clear()
                    showQueueDialog()
                }
            }

        }
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        logger.d("onFragmentStopped Fragment:${f::class.java}")
        super.onFragmentStopped(fm, f)
        if (mWeakReferenceFragment == f && f !is IDialogQ) {
            mWeakReferenceFragment = null
        }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        logger.d("onFragmentDestroyed Fragment:${f::class.java}")
        super.onFragmentDestroyed(fm, f)
        if (f !is IDialogQ) {
            f.deWatchFragment()
        }
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

    private fun jobCancelAndReset(callback: (() -> Unit)? = null) {
        if (isShow.get()) {
            logger.i("jobCancelAndReset show")
        } else {
            logger.i("jobCancelAndReset cancel")
            job?.cancel()
            callback?.invoke()
        }
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
        jobCancelAndReset()
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
                                logger.i("action common showDialog")
                                mWeakReferenceActivity?.let {
                                    data.showDialog(it)
                                    logger.i("action common showDialog end")
                                    return@apply
                                }
                            }

                            fragmentClazz != null && data.bindFragment().contains(fragmentClazz) &&
                                    activityClazz != null && data.bindActivity()
                                .contains(activityClazz) -> {
                                mWeakReferenceFragment?.activity?.javaClass?.kotlin?.let {
                                    if (data.bindActivity().contains(it)) {
                                        logger.i("action bindFragment bindActivity contains showDialog")
                                        mWeakReferenceActivity?.let {
                                            data.showDialog(it)
                                            logger.i("action bindFragment bindActivity contains showDialog end")
                                            return@apply
                                        }
                                    }
                                }
                                logger.i("action bindFragment bindActivity contains queue.poll()")
                                data?.let { deFragmentList.add(it) }
                                data = queue.poll()
                            }

                            activityClazz != null && data.bindActivity()
                                .contains(activityClazz) -> {
                                if (data.bindFragment().isNotEmpty()) {
                                    logger.i("action bindActivity contains queue.poll()")
                                    data?.let { deFragmentList.add(it) }
                                    data = queue.poll()
                                } else {
                                    logger.i("action bindActivity contains showDialog")
                                    mWeakReferenceActivity?.let {
                                        data.showDialog(it)
                                        logger.i("action bindActivity contains showDialog end")
                                        return@apply
                                    }
                                }
                            }

                            fragmentClazz != null && data.bindFragment()
                                .contains(fragmentClazz) -> {
                                if (data.bindActivity().isNotEmpty()) {
                                    logger.i("action bindFragment contains queue.poll()")
                                    if (data?.isKeepALive() == true) {
                                        data?.let { deActivityList.add(it) }
                                    }
                                    data = queue.poll()
                                } else {
                                    logger.i("action bindFragment contains showDialog")
                                    mWeakReferenceActivity?.let {
                                        data.showDialog(it)
                                        logger.i("action bindFragment contains showDialog end")
                                        return@apply
                                    }
                                }
                            }

                            data.bindActivity().isNotEmpty() -> {
                                logger.i("action bindActivity queue.poll()")
                                if (data?.isKeepALive() == true) {
                                    data?.let { deActivityList.add(it) }
                                }
                                data = queue.poll()
                            }

                            data.bindFragment().isNotEmpty() -> {
                                logger.i("action bindFragment queue.poll()")
                                data?.let { deFragmentList.add(it) }
                                data = queue.poll()
                            }

                            else -> {
                                logger.i("action else queue.poll()")
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
            val dialog = checkWithDispatchersMain {
                buildDialog(activity, this@showDialog.extra)
            }
            logger.i("showQueueDialog buildDialog end dialog:$dialog")
            if (dialog == null) {
                if (isShow.compareAndSet(true, false)) {
                    showQueueDialog()
                }
                return
            }
            if (dialog !is IDialogQ) {
                if (isShow.compareAndSet(true, false)) {
                    showQueueDialog()
                }
                return
            }
            logger.i("showQueueDialog attachDialogDismiss")
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