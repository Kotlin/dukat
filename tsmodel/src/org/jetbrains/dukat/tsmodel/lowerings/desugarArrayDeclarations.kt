package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class NativeArrayLowering : DeclarationTypeLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        return if ((declaration.value is IdentifierEntity) && (declaration.value.value == "@@ArraySugar")) {
            declaration.copy(value = IdentifierEntity("Array"), params = declaration.params.map { param -> lowerParameterValue(param) })
        } else {
            declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
        }
    }

}

fun PackageDeclaration.desugarArrayDeclarations(): PackageDeclaration {
    return org.jetbrains.dukat.tsmodel.lowerings.NativeArrayLowering().lowerDocumentRoot(this)
}

fun SourceFileDeclaration.desugarArrayDeclarations() = copy(root = root.desugarArrayDeclarations())

fun SourceSetDeclaration.desugarArrayDeclarations() = copy(sources = sources.map(SourceFileDeclaration::desugarArrayDeclarations))