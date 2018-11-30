package org.jetbrains.dukat.nashorn

import java.util.HashSet

class Set<T> {
    private val hashSet = HashSet<T>()

    val size
        get(): Int {
            return hashSet.size
        }

    constructor() {}

    constructor(elements: Array<T>) {
        for (element in elements) {
            hashSet.add(element)
        }
    }

    fun add(value: T): Set<T> {
        hashSet.add(value)
        return this
    }

    fun clear() {
        hashSet.clear()
    }

    fun delete(value: T): Boolean {
        return hashSet.remove(value)
    }

    fun has(value: T): Boolean {
        return hashSet.contains(value)
    }

    fun forEach(callBack: (value: T, value2: T, set: Set<T>) -> Unit) {
        for (value in hashSet) {
            callBack(value, value, this)
        }
    }
}