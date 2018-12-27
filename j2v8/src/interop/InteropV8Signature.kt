package org.jetbrains.dukat.j2v8.interop

import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object

enum class InteropV8Signature(val javaClass: Class<*>) {
    STRING(String::class.java),
    V8ARRAY(V8Array::class.java),
    V8OBJECT(V8Object::class.java)
}