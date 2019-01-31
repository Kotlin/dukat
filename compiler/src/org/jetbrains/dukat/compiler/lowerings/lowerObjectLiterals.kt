package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun InterfaceDeclaration.isIdentical(otherInterface: InterfaceDeclaration): Boolean {
    return members == otherInterface.members
}

private class LowerObjectLiterals : ParameterValueLowering {

    private val myGeneratedInterfaces = mutableListOf<InterfaceDeclaration>()

    private fun findIdenticalInterface(interfaceDeclaration: InterfaceDeclaration): InterfaceDeclaration? {
        return myGeneratedInterfaces.find { it -> interfaceDeclaration.isIdentical(it) }
    }

    private fun generateInterface(objectLiteral: ObjectLiteralDeclaration): InterfaceDeclaration {
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

    override fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return if (declaration is ObjectLiteralDeclaration) {

            val generatedInterface = generateInterface(declaration)
            if (generatedInterface.members.isEmpty()) {
                TypeDeclaration("Any", emptyList())
            } else {
                TypeDeclaration(generatedInterface.name, emptyList())
            }
        } else declaration
    }

    override fun lowerDocumentRoot(documenRoot: DocumentRootDeclaration): DocumentRootDeclaration {
        return documenRoot.copy(declarations = listOf(myGeneratedInterfaces, lowerTopLevelDeclarations(documenRoot.declarations)).flatten())
    }
}

fun DocumentRootDeclaration.lowerObjectLiterals(): DocumentRootDeclaration {
    return LowerObjectLiterals().lowerDocumentRoot(this)
}