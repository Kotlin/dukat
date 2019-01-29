package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.HeritageClauseDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

private fun TypeAliasDeclaration.canSusbtitute(heritageClause: HeritageClauseDeclaration): Boolean {
    return (aliasName == heritageClause.name) && (typeParameters == heritageClause.typeArguments)
}

private fun TypeAliasDeclaration.canSusbtitute(type: TypeDeclaration): Boolean {
    return (aliasName == type.value)
}

class AstContext {
    private val myInterfaces: MutableMap<String, InterfaceDeclaration> = mutableMapOf()
    private val myClassDeclarations: MutableMap<String, ClassDeclaration> = mutableMapOf()
    private val myTypeAliasDeclaration: MutableList<TypeAliasDeclaration> = mutableListOf()

    fun registerInterface(interfaceDeclaration: InterfaceDeclaration) {
        myInterfaces.put(interfaceDeclaration.name, interfaceDeclaration)
    }

    fun registerClass(classDeclaration: ClassDeclaration) {
        myClassDeclarations.put(classDeclaration.name, classDeclaration)
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

    fun resolveClass(name: String): ClassDeclaration? {
        return myClassDeclarations.get(name)
    }
}