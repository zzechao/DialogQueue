package com.zhouz.myapplication.factory.commonDialog

import android.app.Activity
import android.app.Dialog
import androidx.fragment.app.Fragment
import com.zhouz.dialogqueue.delegate.BaseDialogCommonBuilderFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.dialog.CommonDialog
import com.zhouz.myapplication.fragment.SecondFragment
import kotlin.reflect.KClass

private var index = 0

class CommonDialogFactory4 : BaseDialogCommonBuilderFactory() {

    override val logger = LoggerFactory.getLogger("CommonDialogFactory4")
    override suspend fun buildDialog(activity: Activity, extra: String): Dialog {
        logger.i("CommonDialog builde $extra")
        val dialog = CommonDialog(activity)
        dialog.setTitle("CommonDialog")
        dialog.setContent("测试 CommonDialogFactory4 ${index + 1}")
        index += 1
        dialog.show()
        return dialog
    }

    /**
     * 绑定SecondFragment
     */
    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(SecondFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}