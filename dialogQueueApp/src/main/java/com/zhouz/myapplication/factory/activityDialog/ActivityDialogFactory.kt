package com.zhouz.myapplication.factory.activityDialog

import android.app.Activity
import androidx.activity.ComponentActivity
import com.zhouz.dialogqueue.delegate.BaseDialogActivityBuilderFactory
import com.zhouz.myapplication.dialog.ActivityDialog


/**
 * @author:zhouz
 * @date: 2024/1/15 19:01
 * description：TODO
 */
class ActivityDialogFactory : BaseDialogActivityBuilderFactory() {
    override suspend fun buildDialog(activity: Activity, extra: String): ComponentActivity {
        return ActivityDialog()
    }
}