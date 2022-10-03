package simplePackage

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*

public external abstract class SimpleBase {
    open val field: String
    open var dynamicField: dynamic
}

public external abstract class Simple : SimpleBase, ItemArrayLike<SimpleBase> {
    fun delete(index: Int)
    override fun item(index: Int): SimpleBase?
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun Simple.get(index: Int): SimpleBase? = asDynamic()[index]