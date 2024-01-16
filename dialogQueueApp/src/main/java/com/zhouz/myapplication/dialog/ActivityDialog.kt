package com.zhouz.myapplication.dialog

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zhouz.dialogqueue.IDialogQ
import com.zhouz.myapplication.R
import com.zhouz.myapplication.databinding.ActivityDialogBinding


/**
 * @author:zhouz
 * @date: 2024/1/15 19:03
 * description：activity 弹窗
 */
class ActivityDialog : AppCompatActivity(), IDialogQ {

    private val title get() = intent?.getStringExtra("title") ?: "ActivityDialog"
    private val content get() = intent?.getStringExtra("content")

    private var mBinding: ActivityDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        mBinding = DataBindingUtil.bind((findViewById<ViewGroup>(android.R.id.content)).getChildAt(0))

        mBinding?.mDialog?.mTittleTv?.text = title
        mBinding?.mDialog?.mContentTv?.text = content

        mBinding?.mDialog?.mCancelTv?.setOnClickListener {
            finish()
        }
    }
}