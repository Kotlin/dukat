import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*

public external abstract class A : ItemArrayLike<String> {
    override fun item(index: Int): String
}

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@kotlin.internal.InlineOnly
public inline operator fun A.get(index: Int): String? = asDynamic()[index]