package com.zhouz.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zhouz.dialogqueue.DialogEx
import com.zhouz.dialogqueue.log.LoggerFactory
import com.zhouz.myapplication.ui.main.CommonDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val logger = LoggerFactory.getLogger("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch(Dispatchers.Main) {
            repeat(10) { index ->

                DialogEx.addCommonDialog("$index") { activity, extra ->
                    logger.i("CommonDialog builde $extra")
                    val dialog = CommonDialog(activity)
                    dialog.setContent("测试$extra")
                    dialog.show()
                    dialog
                }
            }
        }

    }
}