package com.zhouz.dialogqueue.log


/**
 * @author:zhouz
 * @date: 2023/12/12 12:06
 * description：构造logger的工厂类
 */
object LoggerFactory {
    fun getLogger(tag: String): DLogger {
        return DLogger(tag)
    }
}