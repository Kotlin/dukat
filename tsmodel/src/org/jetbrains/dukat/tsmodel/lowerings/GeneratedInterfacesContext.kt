package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.getUID
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson

private data class InterfaceDeclarationKey(
        val members: List<MemberDeclaration>,
        val typeParameters: List<TypeParameterDeclaration>,
        val parentEntities: List<HeritageClauseDeclaration>,
        val packageName: String
)

private fun FunctionTypeDeclaration.anonimize(): FunctionTypeDeclaration {
    return copy(parameters = parameters.map { parameter -> parameter.copy(name = "#") })
}

private fun NodeOwner<*>.findOwnerPackage(): PackageDeclaration {
    val ownerPackage = getOwners().first() {
        (it is NodeOwner<*>) && (it.node  is PackageDeclaration)
    } as NodeOwner<PackageDeclaration>

    return ownerPackage.node
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
        parentEntities = parentEntities,
        packageName = packageOwner.packageName
    )
}

private fun areIdentical(aInterface: GeneratedInterfaceDeclaration, bInterface: GeneratedInterfaceDeclaration): Boolean {
    return aInterface.key() == bInterface.key()
}

class GeneratedInterfacesContext {
    private val myGeneratedInterfaces = mutableMapOf<String, GeneratedInterfaceDeclaration>()
    private val myReferences: MutableMap<String, MutableList<GeneratedInterfaceReferenceDeclaration>> = mutableMapOf()

    fun generateInterface(owner: NodeOwner<ParameterValueDeclaration>, ownerUID: String, typeParameters: List<TypeParameterDeclaration>): ParameterValueDeclaration {
        val declaration = owner.node
        val typeParamsSet = typeParameters.map { it.name }.toSet()

        return when (declaration) {
            is FunctionTypeDeclaration -> {
                declaration.copy(
                    parameters = declaration.parameters.map { parameterDeclaration -> parameterDeclaration.copy(type = generateInterface(owner.wrap(parameterDeclaration.type), ownerUID, typeParameters))  }
                )
            }
            is ObjectLiteralDeclaration -> {
                when {
                    declaration.canBeJson() -> TypeDeclaration("Json", emptyList())
                    declaration.members.isEmpty() -> TypeDeclaration("Any", emptyList())
                    else -> {
                        val referenceNode = registerObjectLiteralDeclaration(owner.wrap(declaration), ownerUID, typeParamsSet)
                        referenceNode
                    }
                }
            }
            is UnionTypeDeclaration -> {
                declaration.copy(params = declaration.params.map { param -> generateInterface(owner.wrap(param), ownerUID, typeParameters) } )
            }
            is IntersectionTypeDeclaration -> {
                declaration.copy(params = declaration.params.map { param -> generateInterface(owner.wrap(param), ownerUID, typeParameters) } )
            }
            else -> declaration
        }
    }


    private fun ClassLikeDeclaration.getUID(): String {
        return when (this) {
            is ClassDeclaration -> uid
            is InterfaceDeclaration -> uid
            is GeneratedInterfaceDeclaration -> uid
            else -> throw Exception("can not create uid for ${this}")
        }
    }

    fun lowerMemberDeclaration(owner: NodeOwner<MemberDeclaration>, ownerUid: String, ownerTypeParameters: List<TypeParameterDeclaration>): MemberDeclaration {
        val declaration = owner.node
        return when (declaration) {
            is CallSignatureDeclaration -> {
                val typeParameters = ownerTypeParameters + declaration.typeParameters
                declaration.copy(
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = generateInterface(owner.wrap(param.type), ownerUid, typeParameters))
                        },
                        type = generateInterface(owner.wrap(declaration.type), ownerUid, typeParameters)
                )
            }
            is ConstructorDeclaration -> {
                val typeParameters = ownerTypeParameters + declaration.typeParameters
                declaration.copy(
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = generateInterface(owner.wrap(param.type), ownerUid, typeParameters))
                        }
                )

            }
            is PropertyDeclaration -> {
                val typeParameters = ownerTypeParameters + declaration.typeParameters
                declaration.copy(type = generateInterface(owner.wrap(declaration.type), ownerUid, typeParameters))
            }
            is MethodSignatureDeclaration -> {
                val typeParameters = ownerTypeParameters + declaration.typeParameters
                declaration.copy(
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = generateInterface(owner.wrap(param.type), ownerUid, typeParameters))
                        },
                        type = generateInterface(owner.wrap(declaration.type), ownerUid, typeParameters)
                )
            }
            is FunctionDeclaration -> {
                val typeParameters = ownerTypeParameters + declaration.typeParameters
                declaration.copy(
                        parameters = declaration.parameters.map { param ->
                            param.copy(type = generateInterface(owner.wrap(param.type), ownerUid, typeParameters))
                        },
                        type = generateInterface(owner.wrap(declaration.type), ownerUid, typeParameters)
                )
            }
            else -> declaration
        }
    }


    private fun findIdenticalInterface(interfaceNode: GeneratedInterfaceDeclaration): GeneratedInterfaceDeclaration? {

        myGeneratedInterfaces.forEach { entry ->
            if (areIdentical(interfaceNode, entry.value)) {
                return entry.value
            }
        }

        return null
    }

    private fun ParameterValueDeclaration.findTypeParameterDeclaration(typeParamsSet: Set<String>) : TypeParameterDeclaration? {
        return when (this) {
            is TypeDeclaration -> if (typeParamsSet.contains(value)) TypeParameterDeclaration(value, emptyList())  else null
            else -> null
        }
    }

    private fun registerObjectLiteralDeclaration(owner: NodeOwner<ObjectLiteralDeclaration>, uid: String, typeParamsSet: Set<String>): GeneratedInterfaceReferenceDeclaration {
        val declaration = owner.node
        val typeParams = LinkedHashSet<TypeParameterDeclaration>()

        declaration.members.forEach { member ->
            when (member) {
                is PropertyDeclaration -> {
                    member.type.findTypeParameterDeclaration(typeParamsSet)?.let {
                        typeParams.add(it)
                    }
                }
                is FunctionDeclaration -> {
                    member.type.findTypeParameterDeclaration(typeParamsSet)?.let {
                        typeParams.add(it)
                    }
                }
                is MethodSignatureDeclaration -> {
                    member.type.findTypeParameterDeclaration(typeParamsSet)?.let {
                        typeParams.add(it)
                    }
                }
            }
        }

        val generatedUid = "${uid}_GENERATED"

        val generatedTypeParameters = typeParams.toList()


        val members = declaration.members.map { member -> lowerMemberDeclaration(owner.wrap(member), generatedUid, generatedTypeParameters) }

        val name = "`T$${myGeneratedInterfaces.size}`"
        val interfaceNode =
                GeneratedInterfaceDeclaration(
                        name = name,
                        members = members,
                        typeParameters = generatedTypeParameters,
                        parentEntities = emptyList(),
                        uid = generatedUid,
                        packageOwner = owner.findOwnerPackage()
                )


        val identicalInterface = findIdenticalInterface(interfaceNode)
        return if (identicalInterface == null) {
            myGeneratedInterfaces[name] = interfaceNode
            val referenceNode = GeneratedInterfaceReferenceDeclaration(name, generatedTypeParameters)

            myReferences.getOrPut(uid) { mutableListOf()}.add(referenceNode)

            referenceNode
        } else {
            GeneratedInterfaceReferenceDeclaration(identicalInterface.name, identicalInterface.typeParameters)
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

    fun resolveGeneratedInterfacesFor(node: TypeAliasDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.getUID(), mutableListOf()).map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
    }

}