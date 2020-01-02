package org.jetbrains.dukat.tsLowerings.mergeDuplicates

import java.util.*

internal fun getNewUID() : String {
    return UUID.randomUUID().toString()
}