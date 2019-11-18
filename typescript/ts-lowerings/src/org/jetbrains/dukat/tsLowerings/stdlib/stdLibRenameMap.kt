package org.jetbrains.dukat.tsLowerings.stdlib

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity

object stdLibRenameMap {
    private val renameMap = mapOf(
        Pair(IdentifierEntity("Iterator"), IdentifierEntity("TsStdLib_Iterator"))
    )

    fun resolve(name: NameEntity): NameEntity? {
        return renameMap[name]
    }
}