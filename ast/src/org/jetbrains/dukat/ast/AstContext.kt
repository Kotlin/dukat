package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun TypeAliasDeclaration.canSusbtitute(heritageClause: HeritageClauseDeclaration): Boolean {
    return (aliasName == heritageClause.name) && (typeParameters == heritageClause.typeArguments)
}

private fun TypeAliasDeclaration.canSusbtitute(type: TypeDeclaration): Boolean {
    return (aliasName == type.value)
}

class AstContext {
    private val myInterfaces: MutableMap<String, InterfaceDeclaration> = mutableMapOf()
    private val myClassNodes: MutableMap<String, ClassNode> = mutableMapOf()
    private val myTypeAliasDeclaration: MutableList<TypeAliasDeclaration> = mutableListOf()

    fun registerInterface(interfaceDeclaration: InterfaceDeclaration) {
        myInterfaces.put(interfaceDeclaration.name, interfaceDeclaration)
    }

    fun registerClass(classDeclaration: ClassNode) {
        myClassNodes.put(classDeclaration.name, classDeclaration)
    }

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
            myTypeAliasDeclaration.forEach { typeAlias ->
                if (typeAlias.canSusbtitute(type)) {
                    val typeReference = typeAlias.typeReference

                    if (typeReference is TypeDeclaration) {
                        val params  = typeReference.params.map {param ->
                            resolveTypeAlias(param) ?: param
                        }
                        return typeReference.copy(params = params)
                    }

                    return typeReference
                }
            }
        }
        return null
    }

    fun resolveInterface(name: String): InterfaceDeclaration? = myInterfaces.get(name)

    fun resolveClass(name: String): ClassNode? {
        return myClassNodes.get(name)
    }
}