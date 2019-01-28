package org.jetbrains.dukat.ast

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeAliasDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration

private fun TypeAliasDeclaration.canSusbtitute(iface: InterfaceDeclaration): Boolean {
    return (aliasName == iface.name) && (typeParameters == iface.typeParameters)
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

    fun resolveTypeAlias(iface: InterfaceDeclaration): ParameterValueDeclaration? {
        myTypeAliasDeclaration.forEach { typeAlias ->
            if (typeAlias.canSusbtitute(iface)) {
                return typeAlias.typeReference
            }
        }

        return null
    }

    fun resolveInterface(name: String): InterfaceDeclaration? = myInterfaces.get(name)

    fun resolveClass(name: String): ClassDeclaration? {
        return myClassDeclarations.get(name)
    }
}