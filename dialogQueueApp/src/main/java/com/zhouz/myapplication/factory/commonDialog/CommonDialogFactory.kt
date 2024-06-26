package com.zhouz.myapplication.factory.commonDialog

import android.app.Activity
import android.app.Dialog
import androidx.fragment.app.Fragment
import com.zhouz.dialogqueue.delegate.BaseDialogCommonBuilderFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.MainActivity
import com.zhouz.myapplication.dialog.CommonDialog
import com.zhouz.myapplication.fragment.FirstFragment
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2023/12/13 12:29
 * description：创建普通弹窗的factory，绑定了特定的MainActivity和FirstFragment，isKeepALive设置包活
 */
private var index = 0

class CommonDialogFactory : BaseDialogCommonBuilderFactory() {

    override val logger = LoggerFactory.getLogger("CommonDialogFactory")
    override suspend fun buildDialog(activity: Activity, extra: String): Dialog {
        logger.d("CommonDialog builde $extra")
        val dialog = CommonDialog(activity)
        dialog.setTitle("CommonDialog")
        dialog.setContent("测试 CommonDialogFactory ${index + 1}")
        index += 1
        dialog.show()
        return dialog
    }

    /**
     * 绑定MainActivity
     */
    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(MainActivity::class)
    }

    /**
     * 绑定FirstFragment
     */
    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(FirstFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}