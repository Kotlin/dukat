package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class NativeArrayLowering : DeclarationTypeLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        val value = declaration.value
        return if ((value is IdentifierEntity) && (value.value == "@@ArraySugar")) {
            declaration.copy(value = IdentifierEntity("Array"), params = declaration.params.map { param -> lowerParameterValue(param, owner?.wrap(declaration)) })
        } else {
            declaration.copy(params = declaration.params.map { param -> lowerParameterValue(param, owner?.wrap(declaration)) })
        }
    }

}

fun ModuleDeclaration.desugarArrayDeclarations(): ModuleDeclaration {
    return NativeArrayLowering().lowerDocumentRoot(this, NodeOwner(this, null))
}

fun SourceFileDeclaration.desugarArrayDeclarations() = copy(root = root.desugarArrayDeclarations())

fun SourceSetDeclaration.desugarArrayDeclarations() = copy(sources = sources.map(SourceFileDeclaration::desugarArrayDeclarations))