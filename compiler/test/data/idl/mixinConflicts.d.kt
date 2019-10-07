import kotlin.js.*

external abstract class PerformanceX

external abstract class WindowX {
    open var performance: PerformanceX
}

external abstract class SVGAnimatedStringX

external abstract class SVGAElementX {
    open val href: SVGAnimatedStringX
}