package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

fun SourceSetDeclaration.addPackageName(packageName: NameEntity?): SourceSetDeclaration {
    return this
}