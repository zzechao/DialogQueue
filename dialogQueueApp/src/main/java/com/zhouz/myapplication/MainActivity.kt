package com.zhouz.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.XPopup
import com.zhouz.dialogqueue.DefaultActivityLifecycleCallbacks
import com.zhouz.dialogqueue.DialogEx
import com.zhouz.dialogqueue.IBuildFactory
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.dialog.ActivityDialog
import com.zhouz.myapplication.dialog.CommonDialog
import com.zhouz.myapplication.dialog.FragmentDialog
import com.zhouz.myapplication.dialog.ViewDialog
import com.zhouz.myapplication.factory.Constant
import com.zhouz.myapplication.fragment.FirstFragment
import com.zhouz.myapplication.fragment.SecondFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume


class MainActivity : AppCompatActivity(), IShowFragment {

    private var mSelectId: Int = R.id.btnCommon
    val logger = LoggerFactory.getLogger("MainActivity")

    override var firstFragment: FirstFragment? = null

    override var secondFragment: SecondFragment? = null

    private var factory: MutableList<Class<out IBuildFactory<*>>> = Constant.common

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.first).setOnClickListener {
            showFirstFragment(this)
        }

        findViewById<View>(R.id.second).setOnClickListener {
            showSecondFragment(this)
        }

        findViewById<View>(R.id.toSecondActivity).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            this.startActivity(intent)
        }

        /**
         * 添加不包活的弹窗队列
         */
        findViewById<View>(R.id.bt_add_default_dialog_queue).setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                repeat(10) { index ->
                    when (mSelectId) {
                        R.id.btnCommon -> {
                            DialogEx.addCommonDialog("${index + 1}") { activity, extra ->
                                logger.i("CommonDialog builde $extra")
                                val dialog = CommonDialog(activity)
                                dialog.setTitle("CommonDialog")
                                dialog.setContent("测试 addCommonDialog $extra")
                                dialog.show()
                                dialog
                            }
                        }

                        R.id.btnFragment -> {
                            DialogEx.addFragmentDialog("${index + 1}") { activity, extra ->
                                logger.i("FragmentDialog builde $extra")
                                val content = "测试 addFragmentDialog $extra"
                                val fragmentDialog = FragmentDialog.newInstance(extra, content)
                                fragmentDialog.show((activity as FragmentActivity).supportFragmentManager, "FragmentDialog")
                                fragmentDialog
                            }
                        }

                        R.id.btnActivity -> {
                            DialogEx.addActivityDialog("${index + 1}") { activity, extra ->
                                logger.i("FragmentDialog builde $extra")
                                withTimeout(2000L) {
                                    suspendCancellableCoroutine {
                                        val callbacks = object : DefaultActivityLifecycleCallbacks {
                                            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                                                it.resume(activity as ComponentActivity)
                                                activity.application.unregisterActivityLifecycleCallbacks(this)
                                            }
                                        }
                                        val content = "测试 addActivityDialog $extra"
                                        activity.application.registerActivityLifecycleCallbacks(callbacks)
                                        val intent = Intent(activity, ActivityDialog::class.java)
                                        intent.putExtra("content", content)
                                        activity.startActivity(intent)
                                        it.invokeOnCancellation {
                                            activity.application.unregisterActivityLifecycleCallbacks(callbacks)
                                        }
                                    }
                                }
                            }
                        }

                        R.id.btnView -> {
                            DialogEx.addViewDialog("${index + 1}") { activity, extra ->
                                val content = "测试 addViewDialog $extra"
                                val view = ViewDialog(activity, content)
                                XPopup.Builder(activity)
                                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                                    .isViewMode(true)
                                    .isLightStatusBar(true)// 是否是亮色状态栏，默认false;亮色模式下，状态栏图标和文字是黑色
                                    .customHostLifecycle((activity as AppCompatActivity).lifecycle)
                                    .asCustom(view)
                                    .show()
                                view
                            }
                        }
                    }
                }
            }
        }

        findViewById<View>(R.id.bt_add_factory1).setOnClickListener {
            DialogEx.addDialogBuilderFactory(factory[0].getDeclaredConstructor().newInstance())
        }

        findViewById<View>(R.id.bt_add_factory2).setOnClickListener {
            DialogEx.addDialogBuilderFactory(factory[1].getDeclaredConstructor().newInstance())
        }

        findViewById<View>(R.id.bt_add_factory3).setOnClickListener {
            DialogEx.addDialogBuilderFactory(factory[2].getDeclaredConstructor().newInstance())
        }

        findViewById<View>(R.id.bt_add_factory4).setOnClickListener {
            DialogEx.addDialogBuilderFactory(factory[3].getDeclaredConstructor().newInstance())
        }

        val radgroup = findViewById<RadioGroup>(R.id.radioGroup)
        radgroup.setOnCheckedChangeListener { group, checkedId ->
            val radbtn = findViewById<View>(checkedId) as RadioButton
            mSelectId = radbtn.id
            when (mSelectId) {
                R.id.btnCommon -> {
                    factory = Constant.common
                }

                R.id.btnFragment -> {
                    factory = Constant.fragment
                }

                R.id.btnActivity -> {
                    factory = Constant.activity
                }

                R.id.btnView -> {
                    factory = Constant.view
                }
            }
        }
    }
}