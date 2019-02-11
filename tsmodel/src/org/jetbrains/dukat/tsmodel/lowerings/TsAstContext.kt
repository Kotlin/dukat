package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration

private data class InterfaceDeclarationKey(
        val members: List<MemberDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>
)

private fun FunctionTypeDeclaration.anonimize(): FunctionTypeDeclaration {
    return copy(parameters = parameters.map { parameter -> parameter.copy(name = "#") })
}

private fun GeneratedInterfaceDeclaration.key(): InterfaceDeclarationKey {
    return InterfaceDeclarationKey(
        members = members.map { member ->
            when (member) {
                is FunctionDeclaration -> member.copy(parameters = member.parameters.map { parameter -> parameter.copy(name = "#") })
                is PropertyDeclaration -> member.copy(type = when(member.type) {
                    is FunctionTypeDeclaration -> member.type.anonimize()
                    else -> member.type
                })
                else -> member
            }

        },
        typeParameters = typeParameters,
        parentEntities = parentEntities
    )
}

private fun areIdentical(aInterface: GeneratedInterfaceDeclaration, bInterface: GeneratedInterfaceDeclaration): Boolean {
    return aInterface.key() == bInterface.key()
}

class TsAstContext {
    private val myGeneratedInterfaces = mutableMapOf<String, GeneratedInterfaceDeclaration>()
    private val myReferences: MutableMap<String, MutableList<GeneratedInterfaceReferenceDeclaration>> = mutableMapOf()

    private fun ClassLikeDeclaration.getUID(): String {
        return when (this) {
            is ClassDeclaration -> uid
            is InterfaceDeclaration -> uid
            else -> throw Exception("can not create uid for ${this}")
        }
    }


    fun findIdenticalInterface(interfaceNode: GeneratedInterfaceDeclaration): GeneratedInterfaceDeclaration? {

        myGeneratedInterfaces.forEach { entry ->
            if (areIdentical(interfaceNode, entry.value)) {
                return entry.value
            }
        }

        return null
    }


    fun registerObjectLiteralDeclaration(declaration: ObjectLiteralDeclaration, uid: String): GeneratedInterfaceReferenceDeclaration {

        val name = "`T$${myGeneratedInterfaces.size}`"
        val interfaceNode =
                GeneratedInterfaceDeclaration(
                        name = name,
                        members = declaration.members,
                        typeParameters = emptyList(),
                        parentEntities = emptyList()
                )


        val identicalInterface = findIdenticalInterface(interfaceNode)
        return if (identicalInterface == null) {
            myGeneratedInterfaces.put(name, interfaceNode)
            val referenceNode = GeneratedInterfaceReferenceDeclaration(name)

            myReferences.getOrPut(uid) { mutableListOf()}.add(referenceNode)

            referenceNode
        } else {
            GeneratedInterfaceReferenceDeclaration(identicalInterface.name)
        }
    }

    fun resolveGeneratedInterfacesFor(node: ClassLikeDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.getUID(), mutableListOf()).map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
    }

    fun resolveGeneratedInterfacesFor(node: FunctionDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.uid, mutableListOf()).map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
    }

    fun resolveGeneratedInterfacesFor(node: VariableDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.uid, mutableListOf()).map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
    }

}