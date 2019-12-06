package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering


private fun TypeAliasNode.shouldBeTranslated(): Boolean {
    return when (this.typeReference) {
        is TypeValueNode -> this.typeReference.meta == null
        is TypeParameterNode -> this.typeReference.meta == null
        is FunctionTypeNode -> true
        else -> false
    }
}

private fun DocumentRootNode.filterAliases(astContext: TypeAliasContext): DocumentRootNode {
    val declarationsFiltered = mutableListOf<TopLevelEntity>()
    declarations.forEach { declaration ->
        if (declaration is TypeAliasNode) {
            if (!declaration.shouldBeTranslated()) {
                astContext.registerTypeAlias(declaration)
            } else {
                declarationsFiltered.add(declaration)
            }
        } else if (declaration is DocumentRootNode) {
            declarationsFiltered.add(declaration.filterAliases(astContext))
        } else {
            declarationsFiltered.add(declaration)
        }
    }

    return copy(declarations = declarationsFiltered)
}


private class TypeSpecifierLowering(private val aliasParamMap: Map<NameEntity, ParameterValueDeclaration>) : NodeTypeLowering {

    private fun spec(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        val declarationResolved = when (declaration) {
            is TypeParameterNode -> aliasParamMap.getOrDefault(declaration.name, declaration)
            is TypeValueNode -> aliasParamMap.getOrDefault(declaration.value, declaration)
            else -> declaration
        }

        if (declarationResolved != declaration) {
            declarationResolved.meta = declaration.meta
        }

        return declarationResolved
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is IntersectionMetadata -> {
                IntersectionMetadata(params = declaration.params.map {
                    //TODO: this is our way of avoiding SOE on assigning meta
                    lowerType(it).duplicate<ParameterValueDeclaration>()
                })
            }
            else -> spec(super.lowerType(declaration))
        }
    }
}

private class UnaliasLowering(private val typeAliasContext: TypeAliasContext) : NodeTypeLowering {

    fun lowerDereferenced(declaration: ParameterValueDeclaration, aliasParamMap: Map<NameEntity, ParameterValueDeclaration>): ParameterValueDeclaration {
        return TypeSpecifierLowering(aliasParamMap).lowerType(declaration)
    }

    override fun lowerType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        val declarationResolved = typeAliasContext.dereference(declaration)
        return if (declarationResolved is DereferenceNode) {
            super.lowerType(lowerDereferenced(declarationResolved.dereferenced, declarationResolved.aliasParamsMap))
        } else {
            super.lowerType(declarationResolved)
        }
    }

    private fun ParameterValueDeclaration.unroll(): List<ParameterValueDeclaration> {
        return when (this) {
            is UnionTypeNode -> {
                val paramsUnrolled = params.flatMap { param -> lowerType(param).unroll() }
                //TODO: investigate whether .toList().distinct is a better option
                paramsUnrolled.toSet().toList()
            }
            else -> listOf(lowerType(this))
        }
    }

    override fun lowerUnionTypeNode(declaration: UnionTypeNode): UnionTypeNode {
        return super.lowerUnionTypeNode(declaration.copy(params = declaration.unroll()))
    }
}

fun SourceSetNode.resolveTypeAliases(): SourceSetNode {
    val astContext = TypeAliasContext()

    val sourcesResolved = sources.map { source ->
        source.copy(root = source.root.filterAliases(astContext))
    }

    return copy(sources = sourcesResolved.map { source ->
        source.copy(root = UnaliasLowering(astContext).lowerDocumentRoot(source.root))
    })
}