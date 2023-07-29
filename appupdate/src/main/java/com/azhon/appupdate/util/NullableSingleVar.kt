package com.azhon.appupdate.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
/**
 * 获取值的时候，可以返回null
 * 构造时可以设置初始值，初始值可以为null，设置非Null初始值后，就不能再次赋值，重复赋值会抛出异常。
 * 不设置初始值，后续只能被赋值一次非null的值，重复赋值会抛出异常
 */
class NullableSingleVar<T> (initialValue:T? =null): ReadWriteProperty<Any?, T?> {
    private var field: T? = initialValue

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return field
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.field = if (this.field == null&&value!=null) value else throw IllegalStateException("不能重复赋值")
    }
}