package com.zhouz.myapplication.dialog

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.CenterPopupView
import com.zhouz.dialogqueue.IDialogQ
import com.zhouz.myapplication.R
import com.zhouz.myapplication.databinding.ViewDialogBinding


/**
 * @author:zhouz
 * @date: 2024/1/16 11:18
 * description：view的弹窗
 */
class ViewDialog(context: Context, val content: String) : CenterPopupView(context), IDialogQ {

    private var mBinding: ViewDialogBinding? = null

    override fun getImplLayoutId(): Int {
        return R.layout.view_dialog
    }

    override fun onCreate() {
        super.onCreate()
        mBinding = DataBindingUtil.bind(contentView)
        mBinding?.mDialog?.mTittleTv?.text = "ViewDialog"
        mBinding?.mDialog?.mContentTv?.text = content
        mBinding?.mDialog?.mCancelTv?.setOnClickListener {
            dismiss()
        }
    }
}