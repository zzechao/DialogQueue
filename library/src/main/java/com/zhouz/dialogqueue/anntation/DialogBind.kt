package com.zhouz.dialogqueue.anntation

import android.app.Activity
import com.zhouz.dialogqueue.IPartInterceptor
import kotlin.reflect.KClass

annotation class DialogBind(
    val bindActivity: Array<KClass<out Activity>>, // 绑定的activity
    val isKeepALive: Boolean, // 是否保活
    val dialogPartInterceptors: Array<KClass<out IPartInterceptor>>, // 数据处理或者拦截器
)


