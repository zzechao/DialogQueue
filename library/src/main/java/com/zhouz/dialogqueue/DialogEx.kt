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

/**
 * 弹窗队列处理类
 */
object DialogEx {

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(DialogQueueActivityDeal)
    }

    /**
     * 创建当前activity的队列弹窗ActivityDialog
     */
    fun addActivityDialog(builder: suspend () -> ComponentActivity) {
        val dialogActivityFactory = object : BaseDialogActivityBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): ComponentActivity {
                return builder()
            }
        }
        DialogQueueActivityDeal.addDialogBuilder(dialogActivityFactory)
    }

    /**
     * 创建保活的activity弹窗构建
     */
    fun addActivityDialog(factory: BaseDialogActivityBuilderFactory) {
        DialogQueueActivityDeal.addDialogBuilder(factory)
    }

    /**
     * 创建当前activity的队列弹窗ViewDialog
     */
    fun addViewDialog(builder: suspend () -> View) {
        val dialogViewFactory = object : BaseDialogViewBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): View {
                return builder()
            }
        }
        DialogQueueActivityDeal.addDialogBuilder(dialogViewFactory)
    }

    /**
     * 创建保活的view弹窗构建
     */
    fun addViewDialog(factory: BaseDialogViewBuilderFactory) {
        DialogQueueActivityDeal.addDialogBuilder(factory)
    }

    /**
     * 创建当前activity的队列弹窗fragmentDialog
     */
    fun addFragmentDialog(builder: suspend () -> DialogFragment) {
        val dialogFragmentFactory = object : BaseDialogFragmentBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): DialogFragment {
                return builder()
            }
        }
        DialogQueueActivityDeal.addDialogBuilder(dialogFragmentFactory)
    }

    /**
     * 创建保活的fragment弹窗构建
     */
    fun addFragmentDialog(factory: BaseDialogFragmentBuilderFactory) {
        DialogQueueActivityDeal.addDialogBuilder(factory)
    }

    /**
     * 创建当前activity的队列弹窗Dialog
     */
    fun addCommonDialog(builder: suspend () -> Dialog) {
        val commonDialogFragment = object : BaseDialogCustomBuilderFactory() {
            override suspend fun buildDialog(activity: Activity): Dialog {
                return builder()
            }
        }
        DialogQueueActivityDeal.addDialogBuilder(commonDialogFragment)
    }

    /**
     * 创建保活的dialog弹窗构建
     */
    fun addCommonDialog(factory: BaseDialogCustomBuilderFactory) {
        DialogQueueActivityDeal.addDialogBuilder(factory)
    }

}