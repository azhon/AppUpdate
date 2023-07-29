package com.azhon.appupdate.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 获取值的时候如果为null，抛出异常
 * 构造时可以设置初始值，初始值可以为null，设置非Null初始值后，就不能再次赋值，重复赋值会抛出异常。
 * 不设置初始值，后续只能被赋值一次非null的值，重复赋值会抛出异常
 */
class NotNullSingleVar<T> (initialValue:T? =null): ReadWriteProperty<Any?, T> {
    private var value: T? =initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("还没有被赋值")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null&&value!=null) value else throw IllegalStateException("不能设置为null，或已经有了")
    }
}