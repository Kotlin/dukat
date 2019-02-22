package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class NativeArrayLowering : DeclarationTypeLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        if (declaration.value == "@@ArraySugar") {
            return declaration.copy(value = "Array", params = declaration.params.map { param -> lowerParameterValue(param) })
        } else {
            return declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param) })
        }
    }
}

fun PackageDeclaration.desugarArrayDeclarations(): PackageDeclaration {
    return org.jetbrains.dukat.tsmodel.lowerings.NativeArrayLowering().lowerDocumentRoot(this)
}

fun SourceFileDeclaration.desugarArrayDeclarations() = copy(root = root.desugarArrayDeclarations())