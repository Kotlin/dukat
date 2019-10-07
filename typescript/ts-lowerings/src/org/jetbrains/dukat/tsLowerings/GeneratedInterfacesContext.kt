package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

internal fun Entity.getUID(): String {
    return when (this) {
        is TypeAliasDeclaration -> uid
        is FunctionDeclaration -> uid
        is ClassDeclaration -> uid
        is InterfaceDeclaration -> uid
        is GeneratedInterfaceDeclaration -> uid
        is VariableDeclaration -> uid
        else -> raiseConcern("unknown Entity uid ${this}") { "" };
    }
}

@Suppress("UNCHECKED_CAST")
private fun NodeOwner<*>.findOwnerPackage(): ModuleDeclaration {
    val ownerPackage = getOwners().first() {
        (it is NodeOwner<*>) && (it.node is ModuleDeclaration)
    } as NodeOwner<ModuleDeclaration>

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


private fun ParameterValueDeclaration.findTypeParameterDeclaration(typeParamsSet: Set<NameEntity>): TypeParameterDeclaration? {
    return when (this) {
        is TypeDeclaration -> if (typeParamsSet.contains(value)) TypeParameterDeclaration(value, emptyList(), null) else null
        else -> null
    }
}

private fun ParameterValueDeclaration.resolveTypeParams(typeParamsSet: Set<NameEntity>, generatedTypeParams: LinkedHashSet<TypeParameterDeclaration>) {
    findTypeParameterDeclaration(typeParamsSet)?.let {
        generatedTypeParams.add(it)
    }
}


class GeneratedInterfacesContext {
    private val myGeneratedInterfaces = mutableMapOf<NameEntity, GeneratedInterfaceDeclaration>()
    private val myReferences: MutableMap<String, MutableList<GeneratedInterfaceReferenceDeclaration>> = mutableMapOf()

    private fun findIdenticalInterface(interfaceNode: GeneratedInterfaceDeclaration): GeneratedInterfaceDeclaration? {

        myGeneratedInterfaces.forEach { entry ->
            if (interfaceNode.isIdenticalTo(entry.value)) {
                return entry.value
            }
        }

        return null
    }

    internal fun registerObjectLiteralDeclaration(owner: NodeOwner<ObjectLiteralDeclaration>, uid: String, typeParamsSet: Set<NameEntity>): GeneratedInterfaceReferenceDeclaration {
        val declaration = owner.node
        val typeParams = LinkedHashSet<TypeParameterDeclaration>()

        declaration.members.forEach { member ->
            when (member) {
                is CallSignatureDeclaration -> {
                    member.parameters.forEach { param -> param.type.resolveTypeParams(typeParamsSet, typeParams) }
                    member.type.resolveTypeParams(typeParamsSet, typeParams)
                }
                is PropertyDeclaration -> {
                    member.type.resolveTypeParams(typeParamsSet, typeParams)
                }
                is FunctionDeclaration -> {
                    member.type.resolveTypeParams(typeParamsSet, typeParams)
                }
                is MethodSignatureDeclaration -> {
                    member.parameters.forEach { param -> param.type.resolveTypeParams(typeParamsSet, typeParams) }
                    member.type.resolveTypeParams(typeParamsSet, typeParams)
                }
            }
        }

        val generatedUid = "${uid}_GENERATED"

        val generatedTypeParameters = typeParams.toList()

        val name = IdentifierEntity("`T$${myGeneratedInterfaces.size}`")
        val interfaceNode =
                GeneratedInterfaceDeclaration(
                        name = name,
                        members = declaration.members,
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

    private fun resolveGeneratedInterfacesFor(node: ClassLikeDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.getUID(), mutableListOf()).mapNotNull { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }
    }

    private fun resolveGeneratedInterfacesFor(node: FunctionDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.uid, mutableListOf()).mapNotNull { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }
    }

    private fun resolveGeneratedInterfacesFor(node: VariableDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.uid, mutableListOf()).mapNotNull { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }
    }

    private fun resolveGeneratedInterfacesFor(node: TypeAliasDeclaration): List<GeneratedInterfaceDeclaration> {
        return myReferences.getOrDefault(node.uid, mutableListOf()).mapNotNull { referenceNode -> myGeneratedInterfaces.get(referenceNode.name) }
    }

    private fun introduceGeneratedEntities(declaration: TopLevelEntity): List<TopLevelEntity> {
        return when (declaration) {
            is ClassLikeDeclaration -> resolveGeneratedInterfacesFor(declaration).flatMap { genInterface -> introduceGeneratedEntities(genInterface) } + listOf(declaration)
            is VariableDeclaration -> resolveGeneratedInterfacesFor(declaration).flatMap { genInterface -> introduceGeneratedEntities(genInterface) } + listOf(declaration)
            is FunctionDeclaration -> resolveGeneratedInterfacesFor(declaration).flatMap { genInterface -> introduceGeneratedEntities(genInterface) } + listOf(declaration)
            is TypeAliasDeclaration -> resolveGeneratedInterfacesFor(declaration).flatMap { genInterface -> introduceGeneratedEntities(genInterface) } + listOf(declaration)
            is ModuleDeclaration -> listOf(introduceGeneratedEntities(declaration))
            else -> listOf(declaration)
        }
    }

    fun introduceGeneratedEntities(packageDeclaration: ModuleDeclaration): ModuleDeclaration {
        val declarations = packageDeclaration.declarations.flatMap { declaration ->
            introduceGeneratedEntities(declaration)
        }
        return packageDeclaration.copy(declarations = declarations)
    }
}