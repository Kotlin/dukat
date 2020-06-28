package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private fun TypeAliasDeclaration.shouldBeTranslated(): Boolean {
    return when (this.typeReference) {
        is TypeDeclaration -> this.typeReference.meta == null
        is TypeParamReferenceDeclaration -> this.typeReference.meta == null
        is FunctionTypeDeclaration -> true
        else -> false
    }
}

private fun ModuleDeclaration.filterAliases(astContext: TypeAliasContext): ModuleDeclaration {
    val declarationsFiltered = mutableListOf<TopLevelDeclaration>()
    declarations.forEach { declaration ->
        if (declaration is TypeAliasDeclaration) {
            if (!declaration.shouldBeTranslated()) {
                astContext.registerTypeAlias(declaration)
            } else {
                declarationsFiltered.add(declaration)
            }
        } else if (declaration is ModuleDeclaration) {
            declarationsFiltered.add(declaration.filterAliases(astContext))
        } else {
            declarationsFiltered.add(declaration)
        }
    }

    return copy(declarations = declarationsFiltered)
}


private class ResolveTypeAliasesLowering(private val typeAliasContext: TypeAliasContext) : DeclarationLowering {
    private fun resolveType(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return typeAliasContext.dereference(declaration)
    }

    override fun lowerParameterValue(declaration: ParameterValueDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        return super.lowerParameterValue(this.resolveType(declaration), owner)
    }

    private fun ParameterValueDeclaration.unroll(): List<ParameterValueDeclaration> {
        return when (this) {
            is UnionTypeDeclaration -> {
                val paramsUnrolled = params.flatMap { param -> resolveType(param).unroll() }
                //TODO: investigate whether .toList().distinct is a better option
                paramsUnrolled.toSet().toList()
            }
            else -> listOf(resolveType(this))
        }
    }

    override fun lowerUnionTypeDeclaration(declaration: UnionTypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): ParameterValueDeclaration {
        return super.lowerUnionTypeDeclaration(declaration.copy(params = declaration.unroll()), owner)
    }
}


private fun SourceSetDeclaration.resolveTypeAliases(): SourceSetDeclaration {
    val astContext = TypeAliasContext()

    val sourcesResolved = sources.map { source ->
        source.copy(root = source.root.filterAliases(astContext))
    }

    return copy(sources = sourcesResolved.map { source ->
        source.copy(root = ResolveTypeAliasesLowering(astContext).lowerSourceDeclaration(source.root))
    })
}

class ResolveTypeAliases : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.resolveTypeAliases()
    }
}