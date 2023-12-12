package com.zhouz.dialogqueue.log

/**
 * @author:zhouzechao
 * @date: 2/24/21
 * description：日志文件模块
 */
interface ILog {

    fun v(tag: String, message: () -> Any?) {}

    fun v(tag: String, message: String, vararg args: Any?) {}

    fun d(tag: String, message: () -> Any?) {}

    fun d(tag: String, message: String, vararg args: Any?) {}

    fun i(tag: String, message: () -> Any?) {}

    fun i(tag: String, message: String, vararg args: Any?) {}

    fun i(message: String, vararg args: Any?) {}

    fun w(tag: String, message: () -> Any?) {}

    fun w(tag: String, message: String, vararg args: Any?) {}


    fun e(tag: String, message: () -> Any?, error: Throwable? = null) {}

    fun e(tag: String, message: String, error: Throwable? = null, vararg args: Any?) {}
}