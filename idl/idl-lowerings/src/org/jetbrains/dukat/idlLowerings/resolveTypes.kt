package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.panic.raiseConcern

private class TypeResolver : IDLLowering {

    private val resolvedUnionTypes: MutableSet<String> = mutableSetOf()
    private val failedToResolveUnionTypes: MutableSet<String> = mutableSetOf()
    private val dependenciesToAdd: MutableMap<String, MutableSet<IDLSingleTypeDeclaration>> = mutableMapOf()

    private var file: IDLFileDeclaration = IDLFileDeclaration("", listOf())

    fun getDependencies(declaration: IDLInterfaceDeclaration): List<IDLSingleTypeDeclaration> {
        return dependenciesToAdd[declaration.name]?.toList() ?: listOf()
    }

    private fun processUnionType(unionType: IDLUnionTypeDeclaration) {
        val newDependenciesToAdd: MutableMap<String, MutableSet<IDLSingleTypeDeclaration>> = mutableMapOf()
        for (member in unionType.unionMembers) {
            when (member) {
                is IDLUnionTypeDeclaration -> {
                    if (member.name in failedToResolveUnionTypes) {
                        failedToResolveUnionTypes += unionType.name
                        return
                    }
                    if (member.name !in resolvedUnionTypes) {
                        processUnionType(member)
                    }
                    newDependenciesToAdd.putIfAbsent(member.name, mutableSetOf())
                    newDependenciesToAdd[member.name]!!.add(IDLSingleTypeDeclaration(
                            name = unionType.name,
                            typeParameter = null,
                            nullable = false
                    ))
                }
                is IDLSingleTypeDeclaration -> {
                    if (member.typeParameter != null || !file.containsInterface(member.name)) {
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
                        nullable = declaration.nullable
                )
                in failedToResolveUnionTypes -> IDLSingleTypeDeclaration(
                        name = "\$dynamic",
                        typeParameter = null,
                        nullable = false
                )
                else -> raiseConcern("unprocessed UnionTypeDeclaration: $this") { declaration }
            }
        }
        if (declaration is IDLSingleTypeDeclaration) {
            return if (!declaration.isPrimitive() && !file.containsInterface(declaration.name)) {
                IDLSingleTypeDeclaration("\$dynamic", null, false)
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

    override fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        file = fileDeclaration
        val newFileDeclaration = super.lowerFileDeclaration(fileDeclaration)
        return newFileDeclaration.copy(
                declarations = newFileDeclaration.declarations + resolvedUnionTypes.map {
                    IDLInterfaceDeclaration(
                            name = it,
                            attributes = listOf(),
                            constants = listOf(),
                            operations = listOf(),
                            primaryConstructor = null,
                            constructors = listOf(),
                            parents = listOf(),
                            extendedAttributes = listOf(
                                    IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject")
                            ),
                            callback = false,
                            generated = true
                    )
                }
        )
    }
}

private class DependencyResolver(val typeResolver: TypeResolver) : IDLLowering {

    override fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                parents = declaration.parents + typeResolver.getDependencies(declaration)
        )
    }
}

fun IDLFileDeclaration.resolveTypes(): IDLFileDeclaration {
    val typeResolver = TypeResolver()
    val dependencyResolver = DependencyResolver(typeResolver)
    return dependencyResolver.lowerFileDeclaration(
            typeResolver.lowerFileDeclaration(this)
    )
}