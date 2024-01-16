package com.zhouz.dialogqueue

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.view.View
import androidx.activity.ComponentActivity
import androidx.fragment.app.DialogFragment
import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogCommonBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogFragmentBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogViewBuilderFactory
import com.zhouz.dialogqueue.log.DefaultLog
import com.zhouz.dialogqueue.log.ILogger
import com.zhouz.dialogqueue.log.LoggerFactory

/**
 * @author:zhouzechao
 * @date: 2/24/21
 * 弹窗队列组装
 */
object DialogEx {

    var logger: ILogger = LoggerFactory.getLogger("DialogEx")

    var log = DefaultLog()

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(DialogQueueActivityDeal)
    }

    /**
     * 创建保活的activity弹窗构建
     */
    fun addDialogBuilderFactory(factory: IBuildFactory<*>): Int {
        DialogQueueActivityDeal.addDialogBuilder(factory)
        return factory.dialogID
    }

    /**
     * 创建当前activity的队列弹窗ActivityDialog
     */
    fun addActivityDialog(extra: String = "", builder: suspend (Activity, String) -> ComponentActivity): Int {
        val dialogActivityFactory = object : BaseDialogActivityBuilderFactory() {
            override suspend fun buildDialog(activity: Activity, extra: String): ComponentActivity {
                return builder(activity, extra)
            }
        }
        dialogActivityFactory.extra = extra
        return addDialogBuilderFactory(dialogActivityFactory)
    }


    /**
     * 创建当前activity的队列弹窗ViewDialog
     */
    fun addViewDialog(extra: String = "", builder: suspend (Activity, String) -> View): Int {
        val dialogViewFactory = object : BaseDialogViewBuilderFactory() {
            override suspend fun buildDialog(activity: Activity, extra: String): View {
                return builder(activity, extra)
            }
        }
        dialogViewFactory.extra = extra
        return addDialogBuilderFactory(dialogViewFactory)
    }


    /**
     * 创建当前activity的队列弹窗fragmentDialog
     */
    fun addFragmentDialog(extra: String = "", builder: suspend (Activity, String) -> DialogFragment): Int {
        val dialogFragmentFactory = object : BaseDialogFragmentBuilderFactory() {
            override suspend fun buildDialog(activity: Activity, extra: String): DialogFragment {
                return builder(activity, extra)
            }
        }
        dialogFragmentFactory.extra = extra
        return addDialogBuilderFactory(dialogFragmentFactory)
    }

    /**
     * 创建当前activity的队列弹窗Dialog
     */
    fun addCommonDialog(extra: String = "", builder: suspend (Activity, String) -> Dialog): Int {
        val commonDialogFragment = object : BaseDialogCommonBuilderFactory() {
            override suspend fun buildDialog(activity: Activity, extra: String): Dialog {
                return builder.invoke(activity, extra)
            }
        }
        commonDialogFragment.extra = extra
        return addDialogBuilderFactory(commonDialogFragment)
    }


    /**
     * 去除弹窗
     */
    fun removeFloatDialog(dialogID: Int) {
        logger.i("removeFloatDialog dialogId:$dialogID")
        DialogQueueActivityDeal.removeFloatDialog(dialogID)
    }

    /**
     * 暂停显示弹窗,只添加到队列
     */
    fun pause() {

    }

    /**
     * 恢复显示弹窗
     */
    fun resume() {

    }
}