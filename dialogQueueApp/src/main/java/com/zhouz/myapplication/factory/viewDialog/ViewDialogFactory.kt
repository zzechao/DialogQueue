package com.zhouz.myapplication.factory.viewDialog

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.zhouz.dialogqueue.delegate.BaseDialogViewBuilderFactory
import com.zhouz.myapplication.MainActivity
import com.zhouz.myapplication.dialog.ViewDialog
import com.zhouz.myapplication.fragment.FirstFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.reflect.KClass


/**
 * @author:zhouz
 * @date: 2024/1/15 19:01
 * description：创建指定场景的factory
 */
private var index = 0

class ViewDialogFactory : BaseDialogViewBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): View {
        val content = "测试 ViewDialogFactory ${index + 1}"
        index += 1
        return withTimeout(2000L) {
            suspendCancellableCoroutine { con ->
                val view = ViewDialog(activity, content)
                view.doOnAttach {
                    con.resume(it)
                }
                XPopup.Builder(activity)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .isViewMode(true)
                    .isLightStatusBar(true)// 是否是亮色状态栏，默认false;亮色模式下，状态栏图标和文字是黑色
                    .customHostLifecycle((activity as AppCompatActivity).lifecycle)
                    .asCustom(view)
                    .show()
                con.invokeOnCancellation {
                    view.dismiss()
                }
            }
        }
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
        return arrayOf(MainActivity::class)
    }

    /**
     * 绑定FirstFragment
     */
    override fun bindFragment(): Array<KClass<out Fragment>> {
        return arrayOf(FirstFragment::class)
    }

    override fun isKeepALive(): Boolean {
        return true
    }
}