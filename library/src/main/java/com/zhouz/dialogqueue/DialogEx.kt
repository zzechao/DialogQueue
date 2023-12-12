package com.zhouz.dialogqueue

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.DialogFragment
import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogCustomBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogFragmentBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogViewBuilderFactory
import com.zhouz.dialogqueue.log.DefaultLog
import com.zhouz.dialogqueue.log.ILog
import com.zhouz.dialogqueue.log.ILogger
import com.zhouz.dialogqueue.log.LoggerFactory

/**
 * 弹窗队列处理类
 */
object DialogEx {

    var logger: ILogger = LoggerFactory.getLogger("DialogEx")

    var log = DefaultLog()

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(DialogQueueActivityDeal)
    }

    /**
     * 创建当前activity的队列弹窗ActivityDialog
     */
    fun addActivityDialog(builder: suspend () -> ComponentActivity): Int {
        val dialogActivityFactory = object : BaseDialogActivityBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): ComponentActivity {
                return builder()
            }
        }
        return addActivityDialog(dialogActivityFactory)
    }

    /**
     * 创建保活的activity弹窗构建
     */
    fun addActivityDialog(factory: BaseDialogActivityBuilderFactory): Int {
        DialogQueueActivityDeal.addDialogBuilder(factory)
        return factory.dialogId()
    }

    /**
     * 创建当前activity的队列弹窗ViewDialog
     */
    fun addViewDialog(builder: suspend () -> View): Int {
        val dialogViewFactory = object : BaseDialogViewBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): View {
                return builder()
            }
        }
        return addViewDialog(dialogViewFactory)
    }

    /**
     * 创建保活的view弹窗构建
     */
    fun addViewDialog(factory: BaseDialogViewBuilderFactory): Int {
        DialogQueueActivityDeal.addDialogBuilder(factory)
        return factory.dialogId()
    }

    /**
     * 创建当前activity的队列弹窗fragmentDialog
     */
    fun addFragmentDialog(builder: suspend () -> DialogFragment): Int {
        val dialogFragmentFactory = object : BaseDialogFragmentBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): DialogFragment {
                return builder()
            }
        }
        return addFragmentDialog(dialogFragmentFactory)
    }

    /**
     * 创建保活的fragment弹窗构建
     */
    fun addFragmentDialog(factory: BaseDialogFragmentBuilderFactory): Int {
        DialogQueueActivityDeal.addDialogBuilder(factory)
        return factory.dialogId()
    }

    /**
     * 创建当前activity的队列弹窗Dialog
     */
    fun addCommonDialog(builder: suspend (Activity) -> Dialog): Int {
        val commonDialogFragment = object : BaseDialogCustomBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): Dialog {
                return builder(activity)
            }
        }
        return addCommonDialog(commonDialogFragment)
    }

    /**
     * 创建保活的dialog弹窗构建
     */
    fun addCommonDialog(factory: BaseDialogCustomBuilderFactory): Int {
        DialogQueueActivityDeal.addDialogBuilder(factory)
        return factory.dialogId()
    }


    /**
     * 去除弹窗
     */
    fun removeFloatDialog(dialogID: Int) {
        logger.i("removeFloatDialog dialogId:$dialogID")
        DialogQueueActivityDeal.removeFloatDialog(dialogID)
    }
}