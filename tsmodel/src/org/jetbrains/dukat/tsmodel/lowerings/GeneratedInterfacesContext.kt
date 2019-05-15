package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson

private fun NodeOwner<*>.findOwnerPackage(): PackageDeclaration {
    val ownerPackage = getOwners().first() {
        (it is NodeOwner<*>) && (it.node is PackageDeclaration)
    } as NodeOwner<PackageDeclaration>

    return ownerPackage.node
}

private fun ParameterDeclaration.isIdenticalTo(parameterDeclaration: ParameterDeclaration): Boolean {
    return (type == parameterDeclaration.type) &&
            (initializer == parameterDeclaration.initializer) &&
            (vararg == parameterDeclaration.vararg) &&
            (optional == parameterDeclaration.optional)
}

private fun <T> List<T>.isIdenticalTo(list: List<T>, condition: (a: T, b: T) -> Boolean): Boolean {
    if (this.size != list.size) {
        return false
    }

    return this.zip(list).all { (a, b) -> condition(a, b) }
}


private fun FunctionDeclaration.isIdenticalTo(functionDeclaration: FunctionDeclaration): Boolean {
    if (!type.isIdenticalTo(functionDeclaration.type)) {
        return false
    }

    if (!modifiers.isIdenticalTo(functionDeclaration.modifiers) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    if (!typeParameters.isIdenticalTo(functionDeclaration.typeParameters) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    return parameters.isIdenticalTo(functionDeclaration.parameters) { a, b -> a.isIdenticalTo(b) }
}


private fun MethodSignatureDeclaration.isIdenticalTo(methodSignatureDeclaration: MethodSignatureDeclaration): Boolean {
    if (!type.isIdenticalTo(methodSignatureDeclaration.type)) {
        return false
    }

    if (optional != methodSignatureDeclaration.optional) {
        return false
    }

    if (!modifiers.isIdenticalTo(methodSignatureDeclaration.modifiers) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    if (!typeParameters.isIdenticalTo(methodSignatureDeclaration.typeParameters) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    return parameters.isIdenticalTo(methodSignatureDeclaration.parameters) { a, b -> a.isIdenticalTo(b) }
}

private fun FunctionTypeDeclaration.isIdenticalTo(functionTypeDeclaration: FunctionTypeDeclaration): Boolean {
    if (!parameters.isIdenticalTo(functionTypeDeclaration.parameters) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    return (nullable == functionTypeDeclaration.nullable) &&
            (type.isIdenticalTo(functionTypeDeclaration.type))
}

private fun ParameterValueDeclaration.isIdenticalTo(parameterValueDeclaration: ParameterValueDeclaration): Boolean {
    if (this::class != parameterValueDeclaration::class) {
        return false
    }

    if ((this is FunctionTypeDeclaration) && (parameterValueDeclaration is FunctionTypeDeclaration)) {
        return this.isIdenticalTo(parameterValueDeclaration)
    }

    return this == parameterValueDeclaration
}

private fun ModifierDeclaration.isIdenticalTo(modifierDeclaration: ModifierDeclaration): Boolean {
    return this == modifierDeclaration
}

private fun TypeParameterDeclaration.isIdenticalTo(typeParameterDeclaration: TypeParameterDeclaration): Boolean {
    return this == typeParameterDeclaration
}

private fun PropertyDeclaration.isIdenticalTo(propertyDeclaration: PropertyDeclaration): Boolean {
    if (name != propertyDeclaration.name) {
        return false
    }

    if (optional != propertyDeclaration.optional) {
        return false
    }

    if (!typeParameters.isIdenticalTo(propertyDeclaration.typeParameters) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    if (!modifiers.isIdenticalTo(propertyDeclaration.modifiers) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    return type.isIdenticalTo(propertyDeclaration.type)
}

private fun IndexSignatureDeclaration.isIdenticalTo(indexSignatureDeclaration: IndexSignatureDeclaration): Boolean {
    if (!indexTypes.isIdenticalTo(indexSignatureDeclaration.indexTypes) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    return returnType.isIdenticalTo(indexSignatureDeclaration.returnType)
}

private fun MemberEntity.isIdenticalTo(memberDeclaration: MemberEntity): Boolean {
    if (this::class != memberDeclaration::class) {
        return false
    }

    if ((this is FunctionDeclaration) && (memberDeclaration is FunctionDeclaration)) {
        return isIdenticalTo(memberDeclaration)
    }

    if ((this is PropertyDeclaration) && (memberDeclaration is PropertyDeclaration)) {
        return isIdenticalTo(memberDeclaration)
    }

    if ((this is MethodSignatureDeclaration) && (memberDeclaration is MethodSignatureDeclaration)) {
        return isIdenticalTo(memberDeclaration)
    }

    if ((this is IndexSignatureDeclaration) && (memberDeclaration is IndexSignatureDeclaration)) {
        return isIdenticalTo(memberDeclaration)
    }

    return this == memberDeclaration
}

private fun GeneratedInterfaceDeclaration.isIdenticalTo(someInterface: GeneratedInterfaceDeclaration): Boolean {

    if (packageOwner != someInterface.packageOwner) {
        return false
    }

    if (!typeParameters.isIdenticalTo(someInterface.typeParameters) { a, b -> a.isIdenticalTo(b) }) {
        return false
    }

    if (!parentEntities.isIdenticalTo(someInterface.parentEntities) { a, b -> a == b }) {
        return false
    }

    return members.isIdenticalTo(someInterface.members) { a, b -> a.isIdenticalTo(b) }
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
                        parameters = declaration.parameters.map { parameterDeclaration -> parameterDeclaration.copy(type = generateInterface(owner.wrap(parameterDeclaration.type), ownerUID, typeParameters)) }
                )
            }
            is ObjectLiteralDeclaration -> {
                when {
                    declaration.canBeJson() -> TypeDeclaration(IdentifierDeclaration("Json"), emptyList())
                    declaration.members.isEmpty() -> TypeDeclaration(IdentifierDeclaration("Any"), emptyList())
                    else -> {
                        registerObjectLiteralDeclaration(
                                owner.wrap(declaration.copy(members = declaration.members.map { param ->
                                    lowerMemberDeclaration(owner.wrap(param), ownerUID, typeParameters)
                                })),
                                ownerUID,
                                typeParamsSet
                        )
                    }
                }
            }
            is UnionTypeDeclaration -> {
                declaration.copy(params = declaration.params.map { param -> generateInterface(owner.wrap(param), ownerUID, typeParameters) })
            }
            is IntersectionTypeDeclaration -> {
                declaration.copy(params = declaration.params.map { param -> generateInterface(owner.wrap(param), ownerUID, typeParameters) })
            }
            else -> declaration
        }
    }


    private fun ClassLikeDeclaration.getUID(): String {
        return when (this) {
            is ClassDeclaration -> uid
            is InterfaceDeclaration -> uid
            is GeneratedInterfaceDeclaration -> uid
            else -> raiseConcern("can not create uid for ${this}") { "INVALID_UID" }
        }
    }

    fun lowerMemberDeclaration(owner: NodeOwner<MemberEntity>, ownerUid: String, ownerTypeParameters: List<TypeParameterDeclaration>): MemberEntity {
        val declaration = owner.node

        return when (declaration) {
            is IndexSignatureDeclaration -> {
                declaration.copy(returnType = generateInterface(owner.wrap(declaration.returnType), ownerUid, emptyList()))
            }

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
            if (interfaceNode.isIdenticalTo(entry.value)) {
                return entry.value
            }
        }

        return null
    }

    private fun ParameterValueDeclaration.findTypeParameterDeclaration(typeParamsSet: Set<NameEntity>): TypeParameterDeclaration? {
        return when (this) {
            is TypeDeclaration -> if (typeParamsSet.contains(value)) TypeParameterDeclaration(value, emptyList()) else null
            else -> null
        }
    }

    private fun registerObjectLiteralDeclaration(owner: NodeOwner<ObjectLiteralDeclaration>, uid: String, typeParamsSet: Set<NameEntity>): GeneratedInterfaceReferenceDeclaration {
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

            myReferences.getOrPut(uid) { mutableListOf() }.add(referenceNode)

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
        return myReferences.getOrDefault(node.uid, mutableListOf()).map { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }.filterNotNull()
    }

}