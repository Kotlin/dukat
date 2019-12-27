package org.jetbrains.dukat.tsLowerings.mergeDuplicates

private var uid = 0;

internal fun getNewUID() : String {
    return "reintroduced_uid_${uid++}"
}