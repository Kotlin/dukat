package org.jetbrains.dukat.compiler.lowerings.typeAlias

import org.jetbrains.dukat.tsmodel.GenericParamDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

// TODO: TypeAliases should be revisited
private fun IdentifierDeclaration.translate() = value

private fun HeritageSymbolDeclaration.translate(): String {
    return when (this) {
        is IdentifierDeclaration -> translate()
        is PropertyAccessDeclaration -> expression.translate() + "." + name.translate()
        else -> throw Exception("unknown heritage clause ${this}")
    }
}

class TypeAliasContext {

    private fun TypeAliasDeclaration.canSusbtitute(heritageClause: HeritageClauseDeclaration): Boolean {
        return aliasName == heritageClause.name.translate()
    }

    private fun TypeAliasDeclaration.canSusbtitute(type: TypeDeclaration): Boolean {
        if (aliasName == type.value) {
            return true
        }

        return false
    }

    private val myTypeAliasDeclaration: MutableSet<TypeAliasDeclaration> = mutableSetOf()

    fun registerTypeAlias(typeAlias: TypeAliasDeclaration) {
        myTypeAliasDeclaration.add(typeAlias)
    }

    fun resolveTypeAlias(heritageClause: HeritageClauseDeclaration): ParameterValueDeclaration? {
        myTypeAliasDeclaration.forEach { typeAlias ->
            if (typeAlias.canSusbtitute(heritageClause)) {
                return typeAlias.typeReference
            }
        }

        return null
    }

    fun resolveTypeAlias(type: ParameterValueDeclaration): ParameterValueDeclaration? {
        if (type is TypeDeclaration) {

            val typeAlias = myTypeAliasDeclaration.firstOrNull { typeAlias -> typeAlias.canSusbtitute(type) }

            if (typeAlias != null) {
                val typeReference = typeAlias.typeReference

                if (typeReference is TypeDeclaration) {
                    val specifiedTypes = type.params.toMutableList()

                    val params = typeReference.params.map { param ->
                        if (param is GenericParamDeclaration) {
                            specifiedTypes.removeAt(0)
                        } else resolveTypeAlias(param) ?: param
                    }

                    return typeReference.copy(params = params)
                }

                return typeReference
            }

        }
        return null
    }
}