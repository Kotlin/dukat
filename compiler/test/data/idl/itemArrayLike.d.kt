import kotlin.js.*

external abstract class A : ItemArrayLike<String> {
    override fun item(index: Int): String
}

@kotlin.internal.InlineOnly
inline operator fun A.get(index: Int): String? = asDynamic()[index]