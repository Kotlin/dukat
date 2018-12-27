package org.jetbrains.dukat.j2v8.interop

import com.eclipsesource.v8.V8Object

class InteropV8Class(val context: V8Object, val proxyObject: Any) {

    fun method(methodName: String, vararg signatures: InteropV8Signature) : InteropV8Class {
        context.registerJavaMethod(proxyObject, methodName, methodName, signatures.map { it.javaClass }.toTypedArray())
        return this
    }
}