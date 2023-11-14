package com.zhouz.dialogqueue.weak

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author zhouzechao
 * @date 2019-11-07
 * @desc: 弱引用代理
 */
fun <T : Any> weak(): Weak<T> = Weak()

class Weak<T : Any>(initializer: () -> T?) : ReadWriteProperty<Any?, T?> {
    private var weakReference = WeakReference(initializer())

    constructor() : this({ null })

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return weakReference.get()
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        weakReference = WeakReference(value)
    }
}