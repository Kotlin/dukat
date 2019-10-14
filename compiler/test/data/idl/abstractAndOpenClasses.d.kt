import kotlin.js.*

public external interface ShouldBeInterface {
    var a: Int
    val b: Int
}

public external abstract class ShouldBeAbstract {
    open var a: Int
    open val b: Int
}

public external open class ShouldBeOpen {
    var a: Int
    open val b: Int
}

public external open class ShouldBeOpenToo : ShouldBeOpen {
    var c: Int
    open val d: Int
}