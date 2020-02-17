import kotlin.js.*
import org.khronos.webgl.*

public external abstract class PerformanceX

public external abstract class WindowX {
    open var performance: PerformanceX
}

public external abstract class SVGAnimatedStringX

public external abstract class SVGAElementX {
    open val href: SVGAnimatedStringX
}