package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.SimpleMetaData
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private val SIMPLE_META_DATA = SimpleMetaData("this")
private val SELF_REFERENCE_TYPE = TypeDeclaration(IdentifierEntity("Any"), emptyList(), null, false, SIMPLE_META_DATA)

private fun NodeOwner<*>.classLikeOwnerDeclaration(): TopLevelEntity? {
    val topOwner = generateSequence(this) {
        it.owner
    }.lastOrNull { (it.node is ClassLikeDeclaration) || (it.node is FunctionDeclaration) }
    
    return (topOwner?.node as? TopLevelEntity)
}

private fun processTypeParams(typeParams: List<TypeParameterDeclaration>): List<ParameterValueDeclaration> {
    return typeParams.map { TypeDeclaration(it.name, emptyList(), null) }
}

private fun InterfaceDeclaration.convertToTypeSignature(): ParameterValueDeclaration {
    return TypeDeclaration(name, processTypeParams(typeParameters), ReferenceDeclaration(uid), false, SIMPLE_META_DATA)
}

private fun ClassDeclaration.convertToTypeSignature(): ParameterValueDeclaration {
    return TypeDeclaration(name, processTypeParams(typeParameters), ReferenceDeclaration(uid), false, SIMPLE_META_DATA)
}

private class LowerThisTypeLowering : DeclarationLowering {
    override fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {

        return when (declaration) {
            is ThisTypeDeclaration -> {
                when (val contextNode = owner?.classLikeOwnerDeclaration()) {
                    is ClassDeclaration -> contextNode.convertToTypeSignature()
                    is InterfaceDeclaration -> contextNode.convertToTypeSignature()
                    else -> SELF_REFERENCE_TYPE

                }
            }
            else -> super.lowerParameterValue(declaration, owner)
        }
    }
}

class LowerThisType : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFile ->
            sourceFile.copy(root = LowerThisTypeLowering().lowerSourceDeclaration(sourceFile.root, NodeOwner(sourceFile.root, null)))
         })
    }
}