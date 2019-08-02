
import kotlin.js.*

external abstract class A : ItemArrayLike<String> {
    override val length: Int
    override fun item(index: Int): String?
}