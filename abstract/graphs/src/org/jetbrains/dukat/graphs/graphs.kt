package org.jetbrains.dukat.graphs

interface Vertex<T, V: Vertex<T, V>> {
    val adjacents: Iterable<V>
}

interface Graph<T> {
    val vertices: Iterable<T>
}