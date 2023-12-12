package com.zhouz.dialogqueue

/**
 * 拦截器
 */
interface IPartInterceptor {
    suspend fun intercept(): Boolean {
        return false
    }
}