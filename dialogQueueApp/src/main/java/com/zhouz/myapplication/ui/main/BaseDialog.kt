package com.zhouz.myapplication.ui.main

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog


abstract class BaseDialog : AppCompatDialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, theme: Int) : super(context, theme)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    protected val TAG: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(getBackgroundColor()))
        setContentView(onBindLayout())
        initWindow()
        initView()
        initListener()
        initData()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    private fun initWindow() {
        if (window != null) {
            val params = window?.attributes
            params?.gravity = getGravity()
            params?.width = getWidth()
            params?.height = getHeight()
            params?.dimAmount = getDimAmount()
            if (getX() != 0) {
                params?.x = getX()
            }
            if (getY() != 0) {
                params?.y = getY()
            }
            window?.attributes = params

            window?.setBackgroundDrawableResource(getBackgroundColor())
        }
        //设置点击屏幕不消失
        setCanceledOnTouchOutside(isCancelOutside())
        //设置点击返回键不消失
        setCancelable(isCancelOutside())
    }

    abstract fun onBindLayout(): View
    abstract fun initView()
    abstract fun initData()
    abstract fun initListener()

    protected open fun getGravity(): Int {
        return Gravity.CENTER
    }

    protected open fun getBackgroundColor(): Int {
        return android.R.color.transparent
    }

    protected open fun getWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    protected open fun getHeight(): Int {
        return WindowManager.LayoutParams.WRAP_CONTENT
    }

    protected open fun getDimAmount(): Float {
        return 0.2f
    }

    protected open fun getX(): Int {
        return 0
    }

    protected open fun getY(): Int {
        return 0
    }

    protected open fun isCancelOutside(): Boolean {
        return true
    }
}