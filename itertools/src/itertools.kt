
private fun <T> cartesian2(a: List<T>, b: List<T>): List<List<T>> {
    return a.map { ait -> b.map { bit ->
        listOf(ait, bit)
    } }.flatten()
}


private fun <T> cartesian22(a: List<List<T>>, b: List<T>): List<List<T>> {
    return a.map { ait -> b.flatMap { bit ->
        ait + listOf(bit)
    } }
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
            mutableList.fold(cartesian2(a, b)) { acc, c ->
                cartesian22(acc, c)
            }
        }
    }
}

fun main() {
    println(cartesian( listOf("a"), listOf("b"), listOf("c"), listOf("q"), listOf("w")))
}