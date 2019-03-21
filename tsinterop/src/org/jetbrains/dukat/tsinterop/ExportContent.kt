package org.jetbrains.dukat.tsinterop

import java.util.*


open class ExportContent<T>(
        private val exportTable: MutableMap<T, String> = mutableMapOf(),
        private val createKey: (node: T) -> T
) {
    fun getUID(node: T): String? {
        val nodeKey = createKey(node)

        val uid = if (exportTable.contains(nodeKey)) {
            exportTable[nodeKey]
        } else {
            UUID.randomUUID().toString()
        }

        exportTable[nodeKey] = uid!!
        return uid
    }
}

class ExportContentNonGeneric(
        val exportTable: MutableMap<Any, String> = mutableMapOf()
) : ExportContent<Any>(exportTable, { it })