
import kotlin.js.*

external abstract class A {
    var behavior: ScrollBehavior
}
external interface ScrollBehavior {
    companion object
}
inline val ScrollBehavior.Companion.AUTO: ScrollBehavior get() = "auto".asDynamic().unsafeCast<ScrollBehavior>()
inline val ScrollBehavior.Companion.INSTANT: ScrollBehavior get() = "instant".asDynamic().unsafeCast<ScrollBehavior>()
inline val ScrollBehavior.Companion.SMOOTH: ScrollBehavior get() = "smooth".asDynamic().unsafeCast<ScrollBehavior>()
external interface StyleEnum {
    companion object
}
inline val StyleEnum.Companion.EMPTY: StyleEnum get() = "".asDynamic().unsafeCast<StyleEnum>()
inline val StyleEnum.Companion.FIRST_VALUE: StyleEnum get() = "first-value".asDynamic().unsafeCast<StyleEnum>()
inline val StyleEnum.Companion.SECOND_VALUE: StyleEnum get() = "SECOND-VALUE".asDynamic().unsafeCast<StyleEnum>()