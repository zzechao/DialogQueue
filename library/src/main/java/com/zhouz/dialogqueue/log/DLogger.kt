package com.zhouz.dialogqueue.log

import com.zhouz.dialogqueue.DialogEx


/**
 * @author:zhouz
 * @date: 2023/12/12 12:14
 * description：构造类
 */
class DLogger(private val tag: String) : ILogger, ILog {
    override fun i(message: String, vararg args: Any?) {
        DialogEx.log.i(tag, message, args)
    }

    override fun w(message: String, vararg args: Any?) {
        DialogEx.log.w(tag, message, args)
    }

    override fun e(message: String, error: Throwable?, vararg args: Any?) {
        DialogEx.log.e(tag, message, error, args)
    }

    override fun v(message: String, vararg args: Any?) {
        DialogEx.log.v(tag, message, args)
    }

    override fun d(message: String, vararg args: Any?) {
        DialogEx.log.d(tag, message, args)
    }

    override fun i(tag: String, message: () -> Any?) {
        DialogEx.log.i(tag, message)
    }

    override fun i(tag: String, message: String, vararg args: Any?) {
        DialogEx.log.i(tag, message, *args)
    }

    override fun w(tag: String, message: () -> Any?) {
        DialogEx.log.w(tag, message)
    }

    override fun w(tag: String, message: String, vararg args: Any?) {
        DialogEx.log.w(tag, message, *args)
    }

    override fun v(tag: String, message: () -> Any?) {
        DialogEx.log.v(tag, message)
    }

    override fun v(tag: String, message: String, vararg args: Any?) {
        DialogEx.log.v(tag, message, *args)
    }

    override fun d(tag: String, message: () -> Any?) {
        DialogEx.log.d(tag, message)
    }

    override fun d(tag: String, message: String, vararg args: Any?) {
        DialogEx.log.d(tag, message, *args)
    }
}