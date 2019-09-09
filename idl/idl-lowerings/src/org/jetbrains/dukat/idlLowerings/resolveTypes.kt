package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLClassLikeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLEnumDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.isKnown
import org.jetbrains.dukat.idlDeclarations.InterfaceKind
import org.jetbrains.dukat.idlDeclarations.isPrimitive
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.panic.raiseConcern

private class TypeResolver : IDLLowering {

    private val logger: Logging = Logging("resolveTypes")

    private val resolvedUnionTypes: MutableSet<String> = mutableSetOf()
    private val failedToResolveUnionTypes: MutableSet<String> = mutableSetOf()
    private val dependenciesToAdd: MutableMap<String, MutableSet<IDLSingleTypeDeclaration>> = mutableMapOf()

    private var sourceSet: IDLSourceSetDeclaration = IDLSourceSetDeclaration(listOf())

    fun getDependencies(declaration: IDLClassLikeDeclaration): List<IDLSingleTypeDeclaration> {
        return dependenciesToAdd[declaration.name]?.toList() ?: listOf()
    }

    private fun processUnionType(unionType: IDLUnionTypeDeclaration) {
        val newDependenciesToAdd: MutableMap<String, MutableSet<IDLSingleTypeDeclaration>> = mutableMapOf()
        for (member in unionType.unionMembers) {
            when (member) {
                is IDLUnionTypeDeclaration -> {
                    if (member.name !in resolvedUnionTypes && member.name !in failedToResolveUnionTypes) {
                        processUnionType(member)
                    }
                    if (member.name in failedToResolveUnionTypes) {
                        failedToResolveUnionTypes += unionType.name
                        return
                    }
                    newDependenciesToAdd.putIfAbsent(member.name, mutableSetOf())
                    newDependenciesToAdd[member.name]!!.add(IDLSingleTypeDeclaration(
                            name = unionType.name,
                            typeParameter = null,
                            nullable = false
                    ))
                }
                is IDLSingleTypeDeclaration -> {
                    if (member.typeParameter != null || !sourceSet.containsType(member.name)) {
                        failedToResolveUnionTypes += unionType.name
                        return
                    }
                    newDependenciesToAdd.putIfAbsent(member.name, mutableSetOf())
                    newDependenciesToAdd[member.name]!!.add(IDLSingleTypeDeclaration(
                            name = unionType.name,
                            typeParameter = null,
                            nullable = false
                    ))
                }
                is IDLFunctionTypeDeclaration -> {
                    failedToResolveUnionTypes += unionType.name
                    return
                }
            }
        }
        resolvedUnionTypes += unionType.name
        for ((interfaceName, dependencies) in newDependenciesToAdd) {
            dependenciesToAdd.putIfAbsent(interfaceName, mutableSetOf())
            dependenciesToAdd[interfaceName]!!.addAll(dependencies)
        }
    }

    override fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        if (declaration is IDLUnionTypeDeclaration) {
            if (declaration.name !in resolvedUnionTypes && declaration.name !in failedToResolveUnionTypes) {
                processUnionType(declaration)
                declaration.unionMembers.forEach { lowerTypeDeclaration(it) }
            }
            return when (declaration.name) {
                in resolvedUnionTypes -> IDLSingleTypeDeclaration(
                        name = declaration.name,
                        typeParameter = null,
                        nullable = declaration.nullable,
                        comment = declaration.comment
                )
                in failedToResolveUnionTypes -> IDLSingleTypeDeclaration(
                        name = "\$dynamic",
                        typeParameter = null,
                        nullable = false,
                        comment = declaration.comment
                )
                else -> raiseConcern("unprocessed UnionTypeDeclaration: $this") { declaration }
            }
        }
        if (declaration is IDLSingleTypeDeclaration) {
            return if (!declaration.isKnown() && !sourceSet.containsType(declaration.name)) {
                IDLSingleTypeDeclaration(
                        name = "\$dynamic",
                        typeParameter = null,
                        nullable = false,
                        comment = declaration.comment
                )
            } else {
                declaration.copy(typeParameter = declaration.typeParameter?.let { lowerTypeDeclaration(it) })
            }
        }
        if (declaration is IDLFunctionTypeDeclaration) {
            return declaration.copy(
                    returnType = lowerTypeDeclaration(declaration.returnType),
                    arguments = declaration.arguments.map { lowerArgumentDeclaration(it) }
            )
        }
        return declaration
    }

    private fun resolveInheritance(inheritance: IDLSingleTypeDeclaration, ownerName: String): IDLSingleTypeDeclaration? {
        return if (sourceSet.containsType(inheritance.name)) {
            inheritance
        } else {
            logger.warn("Failed to find parent of ${ownerName}: ${inheritance.name}").let { null }
        }
    }

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        val newDeclaration = super.lowerInterfaceDeclaration(declaration)
        return newDeclaration.copy(
                parents = declaration.parents.mapNotNull { resolveInheritance(it, declaration.name) }
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        val newDeclaration = super.lowerDictionaryDeclaration(declaration)
        return newDeclaration.copy(
                parents = declaration.parents.mapNotNull { resolveInheritance(it, declaration.name) }
        )
    }

    override fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        var newFileDeclaration = super.lowerFileDeclaration(fileDeclaration)
        newFileDeclaration = newFileDeclaration.copy(
                declarations = newFileDeclaration.declarations + resolvedUnionTypes.map {
                    IDLUnionDeclaration(
                            name = it,
                            unions = listOf()
                    )
                }
        )
        resolvedUnionTypes.clear()
        return newFileDeclaration
    }

    override fun lowerSourceSetDeclaration(sourceSet: IDLSourceSetDeclaration): IDLSourceSetDeclaration {
        this.sourceSet = sourceSet
        return super.lowerSourceSetDeclaration(sourceSet)
    }
}

private class DependencyResolver(val typeResolver: TypeResolver) : IDLLowering {

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                unions = declaration.unions + typeResolver.getDependencies(declaration)
        )
    }

    override fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        return declaration.copy(
                unions = declaration.unions + typeResolver.getDependencies(declaration)
        )
    }

    override fun lowerEnumDeclaration(declaration: IDLEnumDeclaration): IDLEnumDeclaration {
        return declaration.copy(
                unions = declaration.unions + typeResolver.getDependencies(declaration)
        )
    }

    override fun lowerUnionDeclaration(declaration: IDLUnionDeclaration): IDLUnionDeclaration {
        return declaration.copy(
                unions = declaration.unions + typeResolver.getDependencies(declaration)
        )
    }
}

fun IDLSourceSetDeclaration.resolveTypes(): IDLSourceSetDeclaration {
    val typeResolver = TypeResolver()
    val dependencyResolver = DependencyResolver(typeResolver)
    return dependencyResolver.lowerSourceSetDeclaration(
            typeResolver.lowerSourceSetDeclaration(this)
    )
}