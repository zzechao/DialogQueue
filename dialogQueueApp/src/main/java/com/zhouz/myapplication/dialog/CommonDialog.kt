package com.zhouz.myapplication.dialog

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.SizeUtils
import com.zhouz.myapplication.databinding.CommonDialogBinding
import com.zhouz.myapplication.ui.main.BaseDialog


open class CommonDialog(context: Context) : BaseDialog(context) {
    protected lateinit var mBinding: CommonDialogBinding
    override fun onBindLayout(): View {
        mBinding = CommonDialogBinding.inflate(layoutInflater)
        mBindingBuilder?.invoke(mBinding)
        mBinding.root
        return mBinding.root
    }

    private var mListener: OnDialogClickListener? = null
    private var mDismissListener: OnDismissListener? = null
    private var mMessageListener: OnMessageListener? = null
    var mBindingBuilder: ((CommonDialogBinding) -> Unit)? = null

    private var mShouldShowTitle = true
    private var mTitle: String? = null
    private var mTitleSize = 0f

    private var mContent: String? = null

    private var mConfirmText: String? = null
    private var mCancelText: String? = null

    private var mIsHtml = false // 是否是html文本
    private var mIsShowConfig = false // 是否显示单个按钮

    private var mTag = 0 // 0 点击取消|点击空白区域取消|按物理返回键取消 1 确定后取消

    private var mMsg: Any? = null // 回传数据

    fun setContent(content: String) {
        mContent = content
    }

    fun setTitle(title: String) {
        mTitle = title
    }

    override fun initView() {
        if (mShouldShowTitle) {
            mBinding.mTittleTv.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(mTitle)) {
                mBinding.mTittleTv.text = mTitle
            }
            if (mTitleSize != 0f) {
                mBinding.mTittleTv.textSize = mTitleSize
            }
        } else {
            mBinding.mTittleTv.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(mContent)) {
            if (mIsHtml) {
                mBinding.mContentTv.text = Html.fromHtml(mContent)
            } else {
                mBinding.mContentTv.text = mContent
            }
        }
        if (mIsShowConfig) {
            mBinding.mBottomLayout.visibility = View.GONE

        } else {
            mBinding.mBottomLayout.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(mCancelText)) {
                mBinding.mCancelTv.text = mCancelText
            }
            if (!TextUtils.isEmpty(mConfirmText)) {
                mBinding.mOkTv.text = mConfirmText
            }
        }
    }

    override fun initData() {}

    override fun initListener() {
        mBinding.mCancelTv.setOnClickListener {
            if (mListener != null) {
                mListener?.onCancel()
            }
            mTag = 0
            dismiss()
        }
        mBinding.mOkTv.setOnClickListener {
            if (mListener != null) {
                mListener?.onOk()
            }
            if (mMessageListener != null) {
                mMessageListener?.onMessage(mMsg)
            }
            mTag = 1
            dismiss()
        }
        setOnDismissListener {
            mTag = 0
            if (mDismissListener != null) {
                mDismissListener?.onDismiss(mTag)
            }
        }
    }

    override fun getWidth(): Int {
        return SizeUtils.dp2px(300f)
    }

    override fun dismiss() {
        if (mDismissListener != null) {
            mDismissListener?.onDismiss(mTag)
        }
        super.dismiss()
    }

    interface OnDismissListener {
        fun onDismiss(tag: Int)
    }

    interface OnDialogClickListener {
        fun onCancel()
        fun onOk()
    }

    interface OnMessageListener {
        fun onMessage(msg: Any?)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mListener = null
    }

}