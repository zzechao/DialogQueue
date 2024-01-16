package com.zhouz.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhouz.dialogqueue.DialogEx
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.dialog.CommonDialog
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory2
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory3
import com.zhouz.myapplication.fragment.FirstFragment
import com.zhouz.myapplication.fragment.SecondFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity(), IShowFragment {

    val logger = LoggerFactory.getLogger("MainActivity")

    override var firstFragment: FirstFragment? = null

    override var secondFragment: SecondFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        findViewById<View>(R.id.first).setOnClickListener {
            showFirstFragment(this)
        }

        findViewById<View>(R.id.second).setOnClickListener {
            showSecondFragment(this)
        }

        findViewById<View>(R.id.bt_add_default_dialog_queue).setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                repeat(10) { index ->
                    DialogEx.addCommonDialog("${index + 1}") { activity, extra ->
                        logger.i("CommonDialog builde $extra")
                        val dialog = CommonDialog(activity)
                        dialog.setContent("测试$extra")
                        dialog.show()
                        dialog
                    }
                }
            }
        }

        findViewById<View>(R.id.bt_add_factory1).setOnClickListener {
            DialogEx.addDialogBuilderFactory(CommonDialogFactory())
        }

        findViewById<View>(R.id.bt_add_factory2).setOnClickListener {
            DialogEx.addDialogBuilderFactory(CommonDialogFactory2())
        }

        findViewById<View>(R.id.bt_add_factory3).setOnClickListener {
            DialogEx.addDialogBuilderFactory(CommonDialogFactory3())
        }
    }
}