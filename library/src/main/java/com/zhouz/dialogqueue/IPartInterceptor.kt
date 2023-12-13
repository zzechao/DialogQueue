package com.zhouz.dialogqueue

/**
 * @author:zhouz
 * @date: 2/24/21
 * 拦截器
 */
interface IPartInterceptor {
    suspend fun intercept(): Boolean {
        return false
    }
}