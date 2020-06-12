package org.jetbrains.dukat.graphs

open class Graph<V> {
    private val vertices = mutableMapOf<V, MutableSet<V>>()

    fun addEdge(start: V, end: V? = null): Boolean {
        if (end == null) {
            vertices.getOrPut(start) { mutableSetOf() }
            return true
        }

        if (vertices[end]?.contains(start) == true) {
            return false
        }

        vertices.getOrPut(start) { mutableSetOf() }.add(end)
        return true
    }

    fun getAdjacentVertices(vertex: V): Set<V>? {
        return vertices[vertex]
    }

    fun forEachVertex(handler: (V, Set<V>) -> Unit) {
        vertices.forEach(handler)
    }

    fun areConnected(start: V?, end: V?): Boolean {
        return vertices[start]?.contains(end) == true
    }
}