package org.jetrbains.dukat.nodeLowering.lowerings.typeAlias

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering
import org.jetrbains.dukat.nodeLowering.lowerings.NodeLowering


private fun TypeAliasNode.shouldBeTranslated(): Boolean {
    return when (this.typeReference) {
        is TypeValueNode -> this.typeReference.meta == null
        is TypeParameterNode -> this.typeReference.meta == null
        is FunctionTypeNode -> true
        else -> false
    }
}

private fun ModuleNode.filterAliases(astContext: TypeAliasContext): ModuleNode {
    val declarationsFiltered = mutableListOf<TopLevelNode>()
    declarations.forEach { declaration ->
        if (declaration is TypeAliasNode) {
            if (!declaration.shouldBeTranslated()) {
                astContext.registerTypeAlias(declaration)
            } else {
                declarationsFiltered.add(declaration)
            }
        } else if (declaration is ModuleNode) {
            declarationsFiltered.add(declaration.filterAliases(astContext))
        } else {
            declarationsFiltered.add(declaration)
        }
    }

    return copy(declarations = declarationsFiltered)
}

private class UnaliasLowering(private val typeAliasContext: TypeAliasContext) : NodeTypeLowering {
    private fun resolveType(declaration: TypeNode): TypeNode {
        return typeAliasContext.dereference(declaration)
    }

    override fun lowerType(declaration: TypeNode): TypeNode {
        return super.lowerType(this.resolveType(declaration))
    }

    private fun TypeNode.unroll(): List<ParameterValueDeclaration> {
        return when (this) {
            is UnionTypeNode -> {
                val paramsUnrolled = params.flatMap { param -> resolveType(param).unroll() }
                //TODO: investigate whether .toList().distinct is a better option
                paramsUnrolled.toSet().toList()
            }
            else -> listOf(resolveType(this))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerUnionTypeNode(declaration: UnionTypeNode): UnionTypeNode {
        return super.lowerUnionTypeNode(declaration.copy(params = declaration.unroll() as List<TypeNode>))
    }
}

private fun SourceSetNode.resolveTypeAliases(): SourceSetNode {
    val astContext = TypeAliasContext()

    val sourcesResolved = sources.map { source ->
        source.copy(root = source.root.filterAliases(astContext))
    }

    return copy(sources = sourcesResolved.map { source ->
        source.copy(root = UnaliasLowering(astContext).lowerModuleNode(source.root))
    })
}

class ResolveTypeAliases() : NodeLowering {
    override fun lower(source: SourceSetNode): SourceSetNode {
        return source.resolveTypeAliases()
    }
}