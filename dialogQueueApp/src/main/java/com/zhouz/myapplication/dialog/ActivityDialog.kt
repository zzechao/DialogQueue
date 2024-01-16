package com.zhouz.myapplication.dialog

import androidx.appcompat.app.AppCompatActivity
import com.zhouz.dialogqueue.IDialogQ


/**
 * @author:zhouz
 * @date: 2024/1/15 19:03
 * description：activity 弹窗
 */
class ActivityDialog : AppCompatActivity(), IDialogQ {

    private val title get() = intent?.getStringExtra("title")
    private val content get() = intent?.getStringExtra("content")
}