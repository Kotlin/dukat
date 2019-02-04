package org.jetbrains.dukat.j2v8.interop

import com.eclipsesource.v8.V8Object
import jodd.util.ClassUtil
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.javaType

class InteropV8Class(val context: V8Object, val proxyObject: Any) {

    fun all() {
        proxyObject::class.declaredMemberFunctions.forEach { method ->
            val params = method.parameters.subList(1, method.parameters.size).map { param ->
                ClassUtil.getRawType(param.type.javaType)
            }

            context.registerJavaMethod(proxyObject, method.name, method.name, params.toTypedArray())
        }
    }

    fun method(methodName: String, vararg signatures: InteropV8Signature): InteropV8Class {
        context.registerJavaMethod(proxyObject, methodName, methodName, signatures.map { it.javaClass }.toTypedArray())
        return this
    }
}