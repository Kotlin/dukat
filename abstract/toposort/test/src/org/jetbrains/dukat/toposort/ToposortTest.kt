package org.jetbrains.dukat.toposort

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private fun MutableMap<String, MutableList<String>>.addEdge(a: String, b: String? = null) {
    val av = this.getOrPut(a) { mutableListOf() }
    b?.let { av.add(it) }
}

class ToposortTest {
    @Test
    fun test() {

        val taggedGraph = mutableMapOf<String, MutableList<String>>()
        taggedGraph.addEdge("e")
        taggedGraph.addEdge("d", "e")
        taggedGraph.addEdge("b", "d")
        taggedGraph.addEdge("c", "d")
        taggedGraph.addEdge("a", "b")
        taggedGraph.addEdge("a", "c")
        taggedGraph.addEdge("a", "d")
        taggedGraph.addEdge("a", "e")

        assertEquals(listOf("e", "d", "b", "c", "a"), taggedGraph.keys.toposort { v -> taggedGraph[v]!! })
    }
}