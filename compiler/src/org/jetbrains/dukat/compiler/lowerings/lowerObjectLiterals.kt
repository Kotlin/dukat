package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.extended.ObjectLiteral

private fun InterfaceDeclaration.isIdentical(otherInterface: InterfaceDeclaration): Boolean {
    return members == otherInterface.members
}

private class LowerObjectLiterals : ParameterValueLowering() {

    private val myGeneratedInterfaces = mutableListOf<InterfaceDeclaration>()

    private fun findIdenticalInterface(interfaceDeclaration: InterfaceDeclaration): InterfaceDeclaration? {
        return myGeneratedInterfaces.find { it -> interfaceDeclaration.isIdentical(it) }
    }

    private fun generateInterface(objectLiteral: ObjectLiteral): InterfaceDeclaration {
        val interfaceDeclaration =
                InterfaceDeclaration("`T$${myGeneratedInterfaces.size}`", objectLiteral.members, emptyList(), emptyList())
        val alreadyExistingInterface = findIdenticalInterface(interfaceDeclaration)
        if (alreadyExistingInterface != null) {
            return alreadyExistingInterface
        } else {
            myGeneratedInterfaces.add(interfaceDeclaration)
            return interfaceDeclaration
        }
    }

    override fun lowerParameterValue(declaration: ParameterValue): ParameterValue {
        return if (declaration is ObjectLiteral) {

            val generatedInterface = generateInterface(declaration)
            if (generatedInterface.members.isEmpty()) {
                TypeDeclaration("Any", emptyList())
            } else {
                TypeDeclaration(generatedInterface.name, emptyList())
            }
        } else super.lowerParameterValue(declaration)
    }

    override fun lowerDocumentRoot(documenRoot: DocumentRoot): DocumentRoot {
        return documenRoot.copy(declarations = listOf(myGeneratedInterfaces, lowerDeclarations(documenRoot.declarations)).flatten())
    }
}

fun DocumentRoot.lowerObjectLiterals(): DocumentRoot {
    return LowerObjectLiterals().lowerDocumentRoot(this)
}