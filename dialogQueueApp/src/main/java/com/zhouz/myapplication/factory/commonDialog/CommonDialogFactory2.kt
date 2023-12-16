package com.zhouz.myapplication.factory.commonDialog

import android.app.Activity
import android.app.Dialog
import com.zhouz.dialogqueue.delegate.BaseDialogCustomBuilderFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.SecondActivity
import com.zhouz.myapplication.dialog.CommonDialog
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2023/12/13 12:29
 * description：创建普通弹窗的factory
 */
private var index = 0

class CommonDialogFactory2 : BaseDialogCustomBuilderFactory() {

    override val logger = LoggerFactory.getLogger("CommonDialogFactory2")
    override suspend fun buildDialog(activity: Activity, extra: String): Dialog {
        logger.i("CommonDialog builde $extra")
        val dialog = CommonDialog(activity)
        dialog.setContent("测试 CommonDialogFactory2 ${index + 1}")
        index += 1
        dialog.show()
        return dialog
    }

    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(SecondActivity::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}