package com.zhouz.myapplication

import android.app.Application
import com.zhouz.dialogqueue.DialogEx

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DialogEx.init(this)
    }
}