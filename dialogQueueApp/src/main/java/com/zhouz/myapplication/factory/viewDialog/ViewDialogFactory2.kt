package com.zhouz.myapplication.factory.viewDialog

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.zhouz.dialogqueue.delegate.BaseDialogViewBuilderFactory
import com.zhouz.myapplication.SecondActivity
import com.zhouz.myapplication.dialog.ViewDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/1/16 11:15
 * description：XPopup的view弹窗
 */
class ViewDialogFactory2 : BaseDialogViewBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): View {
        val view = ViewDialog(activity)
        XPopup.Builder(activity)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .isViewMode(true)
            .hasShadowBg(false) // 去掉半透明背景
            .isLightStatusBar(true)// 是否是亮色状态栏，默认false;亮色模式下，状态栏图标和文字是黑色
            .customHostLifecycle((activity as AppCompatActivity).lifecycle)
            .asCustom(view)
            .show()
        return view
    }

    override suspend fun attachDialogDismiss(): Boolean {
        if (mDialog is BasePopupView) {
            withContext(Dispatchers.Main) {
                (mDialog as BasePopupView).popupInfo?.xPopupCallback = object : SimpleCallback() {
                    override fun onDismiss(popupView: BasePopupView?) {
                        mDialogDismissListeners.forEach {
                            it.get()?.invoke()
                        }
                    }
                }
            }
            return true
        }
        return false
    }

    /**
     * 绑定SecondActivity
     */
    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(SecondActivity::class)
    }

    /**
     *
     */
    override fun isKeepALive(): Boolean {
        return true
    }
}