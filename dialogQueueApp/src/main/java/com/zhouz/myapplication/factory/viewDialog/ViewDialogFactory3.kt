package com.zhouz.myapplication.factory.viewDialog

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.zhouz.dialogqueue.delegate.BaseDialogViewBuilderFactory
import com.zhouz.myapplication.SecondActivity
import com.zhouz.myapplication.dialog.ViewDialog
import com.zhouz.myapplication.fragment.SecondFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/1/16 11:15
 * description：XPopup的view弹窗
 */
private var index = 0

class ViewDialogFactory3 : BaseDialogViewBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): View {
        val content = "测试 ViewDialogFactory3 ${index + 1}"
        index += 1
        val view = ViewDialog(activity, content)
        XPopup.Builder(activity)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .isViewMode(true)
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
     * 绑定MainActivity
     */
    override fun bindActivity(): Array<KClass<out Activity>> {
        return arrayOf(SecondActivity::class)
    }

    /**
     * 绑定SecondFragment
     */
    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(SecondFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}