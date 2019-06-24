package org.jetbrains.dukat.tsinterop

import java.util.*


open class ExportContent<T>(
        private val exportTable: MutableMap<T, String> = mutableMapOf()
) {
    private fun createKey(node: T): T {
        return node
    }

    fun getUID(node: T): String? {
        val nodeKey = createKey(node)
        return exportTable.getOrPut(nodeKey) { UUID.randomUUID().toString() }
    }
}