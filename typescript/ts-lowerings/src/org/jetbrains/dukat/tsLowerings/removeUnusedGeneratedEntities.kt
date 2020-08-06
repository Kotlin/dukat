package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun ModuleDeclaration.visitTopLevelNode(visitor: (TopLevelEntity) -> Unit) {
    visitor(this)
    declarations.forEach {
        if (it is ModuleDeclaration) {
            it.visitTopLevelNode(visitor)
        } else {
            visitor(it)
        }
    }
}

private class ParameterValueVisitor(private val visit: (paramValue: ParameterValueDeclaration) -> Unit) : DeclarationLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        visit(declaration)
        return super.lowerTypeDeclaration(declaration, owner)
    }
}

private fun ModuleDeclaration.removeUnusedReferences(referencesToRemove: Set<String>): ModuleDeclaration {
    val declarationsResolved = declarations.filter {
        !((it is InterfaceDeclaration) && referencesToRemove.contains(it.uid))
    }.map {
        when (it) {
            is ModuleDeclaration -> it.removeUnusedReferences(referencesToRemove)
            else -> it
        }
    }

    return copy(declarations = declarationsResolved)
}

private fun ModuleDeclaration.removeUnusedGeneratedEntities(): ModuleDeclaration {
    val typeRefs = mutableSetOf<String>()
    ParameterValueVisitor { value ->
        when (value) {
            //TODO: we are not supposed to have reference declaration up to this point (but we have)
            is GeneratedInterfaceReferenceDeclaration -> {
                typeRefs.add(uid)
            }
            is TypeDeclaration -> {
                value.typeReference?.uid?.let { uid ->
                    typeRefs.add(uid)
                }
            }
        }
    }.lowerSourceDeclaration(this)


    val unusedGeneratedInterfaces = mutableSetOf<String>()

    visitTopLevelNode { topLevel ->
        if ((topLevel is InterfaceDeclaration) && topLevel.uid.endsWith("_GENERATED")) {
            if (!typeRefs.contains(topLevel.uid)) {
                unusedGeneratedInterfaces.add(topLevel.uid)
            }
        }
    }

    return removeUnusedReferences(unusedGeneratedInterfaces)
}

class RemoveUnusedGeneratedEntities : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFile ->
            sourceFile.copy(root = sourceFile.root.removeUnusedGeneratedEntities())
        })
    }
}