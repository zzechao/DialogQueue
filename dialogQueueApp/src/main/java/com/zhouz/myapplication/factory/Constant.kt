package com.zhouz.myapplication.factory

import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.dialogqueue.delegate.BaseDialogViewBuilderFactory
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory2
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory3
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory4
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory2
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory3
import com.zhouz.myapplication.factory.fragmentDialog.FragmentDialogFactory4


/**
 * @author:zhouz
 * @date: 2024/1/15 18:53
 * description：弹窗配置
 */
object Constant {
    private val common = mutableListOf(CommonDialogFactory(), CommonDialogFactory2(), CommonDialogFactory3(), CommonDialogFactory4())

    private val fragment = mutableListOf(
        FragmentDialogFactory(),
        FragmentDialogFactory2(), FragmentDialogFactory3(), FragmentDialogFactory4()
    )

    private val activity = mutableListOf<BaseDialogActivityBuilderFactory>()

    private val view = mutableListOf<BaseDialogViewBuilderFactory>()
}