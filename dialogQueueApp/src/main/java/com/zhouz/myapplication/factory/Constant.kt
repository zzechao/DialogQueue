package com.zhouz.myapplication.factory

import com.zhouz.myapplication.factory.activityDialog.ActivityDialogFactory
import com.zhouz.myapplication.factory.activityDialog.ActivityDialogFactory2
import com.zhouz.myapplication.factory.activityDialog.ActivityDialogFactory3
import com.zhouz.myapplication.factory.activityDialog.ActivityDialogFactory4
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory2
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory3
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory4
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory2
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory3
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory4
import com.zhouz.myapplication.factory.viewDialog.ViewDialogFactory
import com.zhouz.myapplication.factory.viewDialog.ViewDialogFactory2
import com.zhouz.myapplication.factory.viewDialog.ViewDialogFactory3
import com.zhouz.myapplication.factory.viewDialog.ViewDialogFactory4


/**
 * @author:zhouz
 * @date: 2024/1/15 18:53
 * description：弹窗配置
 */
object Constant {
    val common = mutableListOf(
        CommonDialogFactory(), CommonDialogFactory2(), CommonDialogFactory3(), CommonDialogFactory4()
    )

    val fragment = mutableListOf(
        FragmentDialogFactory(), FragmentDialogFactory2(), FragmentDialogFactory3(), FragmentDialogFactory4()
    )

    val activity = mutableListOf(
        ActivityDialogFactory(), ActivityDialogFactory2(), ActivityDialogFactory3(), ActivityDialogFactory4()
    )

    val view = mutableListOf(
        ViewDialogFactory(), ViewDialogFactory2(), ViewDialogFactory3(), ViewDialogFactory4()
    )
}