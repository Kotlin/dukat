
import kotlin.js.*

external interface ShouldBeInterface {
    var a: Int
    val b: Int
}
external abstract class ShouldBeAbstract {
    open var a: Int
    open val b: Int
}
external open class ShouldBeOpen {
    var a: Int
    open val b: Int
}
external open class ShouldBeOpenToo : ShouldBeOpen {
    var c: Int
    open val d: Int
}