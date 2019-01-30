
private fun <T> cartesian2(a: List<T>, b: List<T>): List<List<T>> {
    return a.map { ait -> b.map { bit ->
        listOf(ait, bit)
    } }.flatten()
}


private fun <T> cartesian22(a: List<List<T>>, b: List<T>): List<List<T>> {
    return a.map { ait -> b.map { bit ->
        ait + listOf(bit)
    } }.flatten()
}


// strictly speaking, it's not cartesian, however let it be
fun <T> cartesian(vararg lists: List<T>): List<List<T>> {
    return when(lists.size) {
        0 -> listOf(emptyList())
        1 -> lists[0].map {listOf(it)}
        else -> {
            val mutableList = lists.toMutableList()
            val a = mutableList.removeAt(0)
            val b = mutableList.removeAt(0)
            mutableList.foldRight(cartesian2(a, b)) { c, acc ->
                cartesian22(acc, c)
            }
        }
    }
}

fun main() {
    val a = cartesian2(listOf(1, 2), listOf(2, 3))
    println(a)
    val b = cartesian22(a, listOf(4,5))
    println(b)
    val c = cartesian(listOf(1, 2), listOf(2, 3), listOf(1, 5))
    println(c)
    println(cartesian2(listOf(1), listOf(1)))
}