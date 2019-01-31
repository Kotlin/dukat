package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.compiler.converters.convertIndexSignatureDeclaration
import org.jetbrains.dukat.compiler.converters.convertPropertyDeclaration
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun TypeAliasDeclaration.canSusbtitute(heritageClause: HeritageClauseDeclaration): Boolean {
    return (aliasName == heritageClause.name) && (typeParameters == heritageClause.typeArguments)
}

private fun TypeAliasDeclaration.canSusbtitute(type: TypeDeclaration): Boolean {
    return (aliasName == type.value)
}

private fun MemberDeclaration.convert(owner: ClassLikeDeclaration): List<MemberDeclaration> {
    return when (this) {
        is PropertyDeclaration -> listOf(convertPropertyDeclaration(this, ROOT_CLASS_DECLARATION))
        is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(this, ROOT_CLASS_DECLARATION)
        else -> listOf(this)
    }
}

class AstContext {
    private val myInterfaces: MutableMap<String, InterfaceNode> = mutableMapOf()
    private val myClassNodes: MutableMap<String, ClassNode> = mutableMapOf()
    private val myTypeAliasDeclaration: MutableList<TypeAliasDeclaration> = mutableListOf()

    private val myGeneratedInterfaces = mutableMapOf<String, InterfaceNode>()

    fun registerInterface(interfaceDeclaration: InterfaceNode) {
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

    fun registerObjectLiteralDeclaration(declaration: ObjectLiteralDeclaration): GeneratedInterfaceReferenceNode {

        val name = "`T$${myGeneratedInterfaces.size}`"
        val interfaceDeclaration =
                InterfaceNode(
                        name,
                        declaration.members.map { member -> member.convert(ROOT_CLASS_DECLARATION) }.flatten(),
                        emptyList(),
                        emptyList()
                )


        // TODO: this is a weak place and it's better to think about better solution
        interfaceDeclaration.members.forEach() { member -> if (member is PropertyNode) {
            member.owner = interfaceDeclaration
        } else if (member is MethodNode) {
            member.owner = interfaceDeclaration
        }}

        myGeneratedInterfaces.put(name, interfaceDeclaration)

        return GeneratedInterfaceReferenceNode(name)
    }

    fun resolveGeneratedInterfacesFor(node: ClassLikeNode): List<InterfaceNode> {
        val genInterfaces = node.generatedReferenceNodes.map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
        return genInterfaces
    }

    fun resolveInterface(name: String): InterfaceNode? = myInterfaces.get(name)

    fun resolveClass(name: String): ClassNode? {
        return myClassNodes.get(name)
    }
}