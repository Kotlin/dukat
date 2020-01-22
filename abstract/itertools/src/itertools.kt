
private fun <T> deepFlatten(list: List<T>): List<T> {

    return list.flatMap { e ->
        when (e) {
            is List<*> -> {
                @Suppress("UNCHECKED_CAST")
                deepFlatten(e as List<T>)
            }
            else -> {
                listOf(e)
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
fun <T> cartesian(vararg lists: List<T>): List<List<T>> {
    return when(lists.size) {
        0 -> listOf(emptyList())
        1 -> {
            listOf(lists[0])
        }
        2 -> {
            val (aList, bList) = lists

            if ((aList.isEmpty()) || (bList.isEmpty())) {
                listOf(emptyList())
            } else {
                aList.flatMap { a ->
                    bList.map { b ->
                        listOf(a, b)
                    }
                }

            }
        }
        else -> lists.drop(2).fold(cartesian(lists[0], lists[1])) { acc, a -> cartesian(acc as List<T>, a) }.map { deepFlatten(it) }
    }
}