import kotlin.js.*
import org.khronos.webgl.*

public external abstract class A {
    open var behavior: ScrollBehavior
}

/* please, don't implement this interface! */
@JsName("null")
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public external interface ScrollBehavior {
    companion object
}

public inline val ScrollBehavior.Companion.AUTO: ScrollBehavior get() = "auto".asDynamic().unsafeCast<ScrollBehavior>()

public inline val ScrollBehavior.Companion.INSTANT: ScrollBehavior get() = "instant".asDynamic().unsafeCast<ScrollBehavior>()

public inline val ScrollBehavior.Companion.SMOOTH: ScrollBehavior get() = "smooth".asDynamic().unsafeCast<ScrollBehavior>()

/* please, don't implement this interface! */
@JsName("null")
@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
public external interface StyleEnum {
    companion object
}

public inline val StyleEnum.Companion.EMPTY: StyleEnum get() = "".asDynamic().unsafeCast<StyleEnum>()

public inline val StyleEnum.Companion.FIRST_VALUE: StyleEnum get() = "first-value".asDynamic().unsafeCast<StyleEnum>()

public inline val StyleEnum.Companion.SECOND_VALUE: StyleEnum get() = "SECOND-VALUE".asDynamic().unsafeCast<StyleEnum>()
