package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructSignatureDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private class ReplaceTypesLowering(private val typesToReplace: Map<String, ParameterValueDeclaration>, private val depth: Int) : DeclarationLowering {
    private val newTypesToReplace = mutableMapOf<String, ParameterValueDeclaration>()

    private fun findNewType(type: ParameterValueDeclaration): ParameterValueDeclaration? {
        if (type is TypeDeclaration && type.typeReference != null) {
            val uid = type.typeReference!!.uid
            if (typesToReplace.containsKey(uid)) {
                return typesToReplace.getValue(uid)
            }
        }
        return null
    }

    override fun lowerParameterValue(
        declaration: ParameterValueDeclaration,
        owner: NodeOwner<ParameterOwnerDeclaration>?
    ): ParameterValueDeclaration {
        return findNewType(declaration) ?: declaration
    }

    override fun lowerHeritageClause(
        heritageClause: HeritageClauseDeclaration,
        owner: NodeOwner<ClassLikeDeclaration>?
    ): HeritageClauseDeclaration {
        val reference = heritageClause.typeReference
        if (reference != null && typesToReplace.containsKey(reference.uid)) {
            val newType = typesToReplace.getValue(reference.uid)
            if (newType is TypeDeclaration) {
                return heritageClause.copy(name = newType.value, typeArguments = newType.params, typeReference = newType.typeReference)
            }
        }
        return heritageClause
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>?): VariableDeclaration {
        val newType = findNewType(declaration.type)
        if (newType != null) {
            newTypesToReplace[declaration.uid] = newType
            return declaration.copy(type = newType)
        }
        return declaration
    }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val newSource = super.lower(source)
        return if (depth == 0) {
            ReplaceTypesLowering(newTypesToReplace, depth + 1).lower(newSource)
        } else {
            newSource
        }
    }
}

private class ProcessConstructorInterfacesLowering : DeclarationLowering {

    private val typesToReplace = mutableMapOf<String, ParameterValueDeclaration>()

    private fun determineCommonReturnType(constructors: List<ConstructSignatureDeclaration>): ParameterValueDeclaration? {
        return constructors[0].type
    }

    override fun lowerInterfaceDeclaration(
        declaration: InterfaceDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): InterfaceDeclaration {
        val constructSignatures = declaration.members.filterIsInstance<ConstructSignatureDeclaration>()
        if (constructSignatures.isEmpty()) {
            return declaration
        }
        val commonReturnType = determineCommonReturnType(constructSignatures)
        if (commonReturnType is TypeDeclaration && commonReturnType.value == IdentifierEntity("Array")) {
            typesToReplace[declaration.uid] = commonReturnType
        }
        return declaration
    }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val newSource = super.lower(source)
        return ReplaceTypesLowering(typesToReplace, 0).lower(newSource)
    }
}

class ProcessConstructorInterfaces : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return ProcessConstructorInterfacesLowering().lower(source)
    }
}