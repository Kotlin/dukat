package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetadata
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.compiler.converters.convertIndexSignatureDeclaration
import org.jetbrains.dukat.compiler.converters.convertMethodSignatureDeclaration
import org.jetbrains.dukat.compiler.converters.convertPropertyDeclaration
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
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

private fun MemberDeclaration.convert(owner: ClassLikeNode): List<MemberDeclaration> {
    return when (this) {
        is PropertyDeclaration -> listOf(convertPropertyDeclaration(this, ROOT_CLASS_DECLARATION))
        is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(this, ROOT_CLASS_DECLARATION)
        is MethodSignatureDeclaration -> listOf(convertMethodSignatureDeclaration(this, ROOT_CLASS_DECLARATION))
        else -> listOf(this)
    }
}

private fun MemberDeclaration.copyWithNoOwner() = when (this) {
    is PropertyNode -> copy(owner = ROOT_CLASS_DECLARATION)
    is MethodNode -> copy(owner = ROOT_CLASS_DECLARATION)
    else -> this
}

private fun areIdentical(aInterface: InterfaceNode, bInterface: InterfaceNode): Boolean {

    val aMembers = aInterface.members.map { it.copyWithNoOwner() }
    val bMembers = bInterface.members.map { it.copyWithNoOwner() }

    return aMembers == bMembers
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

    fun findIdenticalInterface(interfaceNode: InterfaceNode): InterfaceNode? {

        myGeneratedInterfaces.forEach { entry ->
            if (areIdentical(interfaceNode, entry.value)) {
                return entry.value
            }
        }

        return null
    }


    private fun ParameterValueDeclaration.lower() : ParameterValueDeclaration {
        return when (this) {
            is ThisTypeDeclaration -> TypeDeclaration("Any", emptyList(), false, ThisTypeInGeneratedInterfaceMetadata())
            else -> this
        }
    }

    private fun InterfaceNode.lower() : InterfaceNode {
        val members = members.map { member ->
            when (member) {
                is PropertyNode -> member.copy(type = member.type.lower())
                is MethodNode -> member.copy(type = member.type.lower())
                else -> member
            }
        }

        return copy(members = members)
    }

    fun registerObjectLiteralDeclaration(declaration: ObjectLiteralDeclaration, generatedReferences: MutableList<GeneratedInterfaceReferenceNode>): GeneratedInterfaceReferenceNode {

        val name = "`T$${myGeneratedInterfaces.size}`"
        val interfaceNode =
                InterfaceNode(
                        name,
                        declaration.members.map { member -> member.convert(ROOT_CLASS_DECLARATION) }.flatten(),
                        emptyList(),
                        emptyList(),
                        mutableListOf(),
                        null,
                        "__NO__UID__"
                ).lower()


        // TODO: this is a weak place and it's better to think about better solution
        interfaceNode.members.forEach() { member ->
            if (member is PropertyNode) {
                member.owner = interfaceNode
            } else if (member is MethodNode) {
                member.owner = interfaceNode
            }
        }


        val identicalInterface = findIdenticalInterface(interfaceNode)
        return if (identicalInterface == null) {
            myGeneratedInterfaces.put(name, interfaceNode)
            val referenceNode = GeneratedInterfaceReferenceNode(name)
            generatedReferences.add(referenceNode)
            referenceNode
        } else {
            GeneratedInterfaceReferenceNode(identicalInterface.name)
        }
    }

    fun resolveGeneratedInterfacesFor(node: ClassLikeNode): List<InterfaceNode> {
        val genInterfaces = node.generatedReferenceNodes.map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
        return genInterfaces
    }

    fun resolveGeneratedInterfacesFor(node: FunctionNode): List<InterfaceNode> {
        val genInterfaces = node.generatedReferenceNodes.map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
        return genInterfaces
    }

    fun resolveInterface(name: String): InterfaceNode? = myInterfaces.get(name)

    fun resolveClass(name: String): ClassNode? {
        return myClassNodes.get(name)
    }
}