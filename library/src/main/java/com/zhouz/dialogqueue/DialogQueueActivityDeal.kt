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

object DialogQueueActivityDeal : FragmentManager.FragmentLifecycleCallbacks(), DefaultActivityLifecycleCallbacks {


    private val logger = LoggerFactory.getLogger("DialogQueueActivityDeal")

    private val dialogIdAtomic = AtomicInteger(0)
    private var mWeakReferenceActivity by weak<Activity>()
    private var mWeakReferenceFragment by weak<Fragment>()
    private var job: Job? = null

    private var loggingExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        logger.e("CoroutineException", "Coroutine exception occurred. $context", throwable)
    }

    private val dispatcher = ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, LinkedBlockingDeque(), object : ThreadFactory {
        private val mThreadId = AtomicInteger(0)

        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format("dialog_thread_%d", mThreadId.getAndIncrement())
            return t
        }
    }).asCoroutineDispatcher()

    val dialogQueueScope = CoroutineScope(SupervisorJob() + dispatcher + loggingExceptionHandler)

    private val isShow = AtomicBoolean(false)

    private val queue = PriorityBlockingQueue<IBuildFactory<out Any>>(20, Comparator { dialog1, dialog2 ->
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
        showQueueDialog()
    }


    /**
     * ==============activity=======================
     */
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
        super.onActivityStopped(activity)
        (activity as? FragmentActivity)?.deWatchFragment()
    }

    override fun onActivityDestroyed(activity: Activity) {
        mWeakReferenceActivity = null
        job?.cancel()
    }

    /**
     * =================fragment ====================
     */
    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        mWeakReferenceFragment = f
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        f.watchFragment()
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
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
        super.onFragmentStopped(fm, f)
        f.deWatchFragment()
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        mWeakReferenceFragment = null
    }


    /**
     * ================watch fragment =================
     */
    private fun FragmentActivity.watchFragment() {
        supportFragmentManager.registerFragmentLifecycleCallbacks(this@DialogQueueActivityDeal, false)
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


        MethodStackTrace.printMethodStack("zzc", "showQueueDialog")
        logger.i("showQueueDialog size:${queue.size}")
        job?.cancel()
        job = dialogQueueScope.launch {
            var data = queue.poll()
            while (data != null) {
                val intercept = data.dialogPartInterceptors().firstOrNull { it.intercept() }
                if (intercept == null) {
                    val activityClazz = mWeakReferenceActivity?.javaClass?.kotlin
                    val fragmentClazz = mWeakReferenceFragment?.javaClass?.kotlin
                    when {
                        data.bindActivity().isEmpty() && data.bindFragment().isEmpty() -> {
                            mWeakReferenceActivity?.let {
                                data.showDialog(it)
                                return@launch
                            }
                        }

                        fragmentClazz != null && data.bindFragment().contains(fragmentClazz) &&
                                activityClazz != null && data.bindActivity().contains(activityClazz) -> {
                            mWeakReferenceActivity?.let {
                                data.showDialog(it)
                                return@launch
                            }
                        }

                        activityClazz != null && data.bindActivity().contains(activityClazz) -> {
                            if (data.bindFragment().isNotEmpty()) {
                                deFragmentList.add(data)
                                data = queue.poll()
                            } else {
                                mWeakReferenceActivity?.let {
                                    data.showDialog(it)
                                    return@launch
                                }
                            }
                        }

                        fragmentClazz != null && data.bindFragment().contains(fragmentClazz) -> {
                            if (data.bindActivity().isNotEmpty()) {
                                if (data.isKeepALive()) {
                                    deActivityList.add(data)
                                }
                                data = queue.poll()
                            } else {
                                mWeakReferenceActivity?.let {
                                    data.showDialog(it)
                                    return@launch
                                }
                            }
                        }


                        else -> {

                        }
                    }
                }
            }
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
                buildDialog(activity)
            }
            logger.i("showQueueDialog attachDialogDismiss dialog:$dialog")
            this.mDialog = dialog
            attachDialogDismiss()
        }
    }

    /**
     * 去除队列的dialogID
     */
    fun removeFloatDialog(dialogID: Int) {
        queue.find { it.dialogId() == dialogID }?.let {
            queue.remove(it)
            return
        }
        deActivityList.find { it.dialogId() == dialogID }?.let {
            deActivityList.remove(it)
            return
        }
        deFragmentList.find { it.dialogId() == dialogID }?.let {
            deFragmentList.remove(it)
            return
        }
    }
}