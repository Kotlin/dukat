import kotlin.js.*

public external abstract class A : ItemArrayLike<String> {
    override fun item(index: Int): String
}

@kotlin.internal.InlineOnly
public inline operator fun A.get(index: Int): String? = asDynamic()[index]