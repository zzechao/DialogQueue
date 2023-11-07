package com.zhouz.dialogqueue

import android.app.Activity
import android.os.Bundle
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

object DialogQueueActivityDeal : DefaultActivityLifecycleCallbacks {

    private val dialogIdAtomic = AtomicInteger(0)

    private val queue =
        PriorityBlockingQueue<IBuildFactory<Any>>(100, Comparator { dialog1, dialog2 ->
            val diff = dialog2.priority - dialog1.priority
            return@Comparator if (diff == 0) {
                val diff2 = dialog1.dialogId - dialog2.dialogId
                diff2 / abs(diff2)
            } else {
                diff / abs(diff)
            }
        })

    fun getDialogId(): Int {
        return dialogIdAtomic.incrementAndGet()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}