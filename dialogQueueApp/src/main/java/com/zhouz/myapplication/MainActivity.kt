package com.zhouz.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhouz.dialogqueue.DialogEx
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.dialog.CommonDialog
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory2
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory3
import com.zhouz.myapplication.factory.commonDialog.CommonDialogFactory4
import com.zhouz.myapplication.fragment.FirstFragment
import com.zhouz.myapplication.fragment.SecondFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), IShowFragment {

    val logger = LoggerFactory.getLogger("MainActivity")

    override var firstFragment: FirstFragment? = null

    override var secondFragment: SecondFragment? = null

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
            DialogEx.addCommonDialog(CommonDialogFactory())
        }

        findViewById<View>(R.id.bt_add_factory2).setOnClickListener {
            DialogEx.addCommonDialog(CommonDialogFactory2())
        }

        findViewById<View>(R.id.bt_add_factory3).setOnClickListener {
            DialogEx.addCommonDialog(CommonDialogFactory3())
        }

        findViewById<View>(R.id.bt_add_factory4).setOnClickListener {
            DialogEx.addCommonDialog(CommonDialogFactory4())
        }

        val radgroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        radgroup.setOnCheckedChangeListener { group, checkedId ->
            val radbtn = findViewById<View>(checkedId) as RadioButton
            Toast.makeText(applicationContext, "按钮组值发生改变,你选了" + radbtn.text, Toast.LENGTH_LONG).show()
        }
    }
}