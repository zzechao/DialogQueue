package com.zhouz.myapplication.factory

import android.app.Activity
import android.app.Dialog
import androidx.fragment.app.Fragment
import com.zhouz.dialogqueue.delegate.BaseDialogCustomBuilderFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.MainActivity
import com.zhouz.myapplication.fragment.FirstFragment
import com.zhouz.myapplication.ui.main.CommonDialog
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2023/12/13 12:29
 * description：创建普通弹窗的factory
 */
class CommonDialogFactory : BaseDialogCustomBuilderFactory() {

    override val logger = LoggerFactory.getLogger("CommonDialogFactory")
    override suspend fun buildDialog(activity: Activity, extra: String): Dialog {
        logger.i("CommonDialog builde $extra")
        val dialog = CommonDialog(activity)
        dialog.setContent("测试$extra")
        dialog.show()
        return dialog
    }

    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(MainActivity::class)
    }

    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(FirstFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}