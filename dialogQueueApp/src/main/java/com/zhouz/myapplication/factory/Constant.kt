package com.zhouz.myapplication.factory

import com.zhouz.dialogqueue.IBuildFactory
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
    val common = mutableListOf<Class<out IBuildFactory<*>>>(
        CommonDialogFactory::class.java, CommonDialogFactory2::class.java, CommonDialogFactory3::class.java, CommonDialogFactory4::class.java
    )

    val fragment = mutableListOf<Class<out IBuildFactory<*>>>(
        FragmentDialogFactory::class.java, FragmentDialogFactory2::class.java, FragmentDialogFactory3::class.java, FragmentDialogFactory4::class.java
    )

    val activity = mutableListOf<Class<out IBuildFactory<*>>>(
        ActivityDialogFactory::class.java, ActivityDialogFactory2::class.java, ActivityDialogFactory3::class.java, ActivityDialogFactory4::class.java
    )

    val view = mutableListOf<Class<out IBuildFactory<*>>>(
        ViewDialogFactory::class.java, ViewDialogFactory2::class.java, ViewDialogFactory3::class.java, ViewDialogFactory4::class.java
    )
}