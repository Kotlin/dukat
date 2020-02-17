package org.w3c.dom.parsing

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*

/**
 * Exposes the JavaScript [DOMParser](https://developer.mozilla.org/en/docs/Web/API/DOMParser) to Kotlin
 */
public external open class DOMParser {
    fun parseFromString(str: String, type: dynamic): Document
}

/**
 * Exposes the JavaScript [XMLSerializer](https://developer.mozilla.org/en/docs/Web/API/XMLSerializer) to Kotlin
 */
public external open class XMLSerializer {
    fun serializeToString(root: Node): String
}

