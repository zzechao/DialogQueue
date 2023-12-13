package com.zhouz.dialogqueue.log

/**
 * @author:zhouz
 * @date: 2/24/21
 * description：日志文件模块
 */
interface ILogger {

    fun v(message: String, vararg args: Any?)

    fun d(message: String, vararg args: Any?)

    fun i(message: String, vararg args: Any?)

    fun w(message: String, vararg args: Any?)

    fun e(message: String, error: Throwable? = null, vararg args: Any?)
}