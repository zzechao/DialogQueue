package com.zhouz.myapplication

import com.zhouz.dialogqueue.DialogQueueActivityDeal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun main(args: Array<String>) {
    val job = DialogQueueActivityDeal.dialogQueueScope.launch(Dispatchers.IO) {
        while (true) {
            delay(1000L)
            println("test")
        }
    }
    Thread.sleep(5000L)
    job.cancel()
}