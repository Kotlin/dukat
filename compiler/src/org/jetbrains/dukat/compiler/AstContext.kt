package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
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


private fun TypeAliasDeclaration.canSusbtitute(heritageClause: HeritageClauseDeclaration): Boolean {
    return aliasName == heritageClause.name.translate()
}

private fun TypeAliasDeclaration.canSusbtitute(type: TypeDeclaration): Boolean {
    return (aliasName == type.value)
}

class AstContext {
    private val myInterfaces: MutableMap<HeritageSymbolDeclaration, InterfaceNode> = mutableMapOf()
    private val myClassNodes: MutableMap<HeritageSymbolDeclaration, ClassNode> = mutableMapOf()
    private val myTypeAliasDeclaration: MutableList<TypeAliasDeclaration> = mutableListOf()

    fun registerInterface(interfaceDeclaration: InterfaceNode) {
        myInterfaces.put(IdentifierDeclaration(interfaceDeclaration.name), interfaceDeclaration)
    }

    fun registerClass(classDeclaration: ClassNode) {
        myClassNodes.put(IdentifierDeclaration(classDeclaration.name), classDeclaration)
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
                        val params = typeReference.params.map { param ->
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


    private fun ParameterValueDeclaration.lower(): ParameterValueDeclaration {
        return when (this) {
            is ThisTypeDeclaration -> TypeDeclaration("Any", emptyList(), false, ThisTypeInGeneratedInterfaceMetaData())
            else -> this
        }
    }

    private fun InterfaceNode.lower(): InterfaceNode {
        val members = members.map { member ->
            when (member) {
                is PropertyNode -> member.copy(type = member.type.lower())
                is MethodNode -> member.copy(type = member.type.lower())
                else -> member
            }
        }

        return copy(members = members)
    }

    fun resolveInterface(name: HeritageSymbolDeclaration): InterfaceNode? = myInterfaces.get(name)

    fun resolveClass(name: HeritageSymbolDeclaration): ClassNode? {
        return myClassNodes.get(name)
    }
}