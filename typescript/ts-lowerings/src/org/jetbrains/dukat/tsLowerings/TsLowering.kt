package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

interface TsLowering : Lowering<SourceSetDeclaration, SourceSetDeclaration>

fun SourceSetDeclaration.lower(vararg lowerings: TsLowering): SourceSetDeclaration {
    return lowerings.fold(this) { sourceSet, tsLowering ->  tsLowering.lower(sourceSet) }
}