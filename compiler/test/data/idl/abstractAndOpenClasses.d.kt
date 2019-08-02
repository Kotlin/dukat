
import kotlin.js.*

external interface ShouldBeInterface {
    var a: Int
    val b: Int
}
external abstract class ShouldBeAbstract {
    var a: Int
    open val b: Int
}
external open class ShouldBeOpen {
    open var a: Int
    open val b: Int
}
external open class ShouldBeOpenToo : ShouldBeOpen {
    open var c: Int
    open val d: Int
}