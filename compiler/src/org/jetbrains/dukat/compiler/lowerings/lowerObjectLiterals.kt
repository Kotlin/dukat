package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration

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
        } else super.lowerParameterValue(declaration)
    }

    override fun lowerDocumentRoot(documenRoot: DocumentRootDeclaration): DocumentRootDeclaration {
        return documenRoot.copy(declarations = listOf(myGeneratedInterfaces, lowerTopLevelDeclarations(documenRoot.declarations)).flatten())
    }
}

fun DocumentRootDeclaration.lowerObjectLiterals(): DocumentRootDeclaration {
    return LowerObjectLiterals().lowerDocumentRoot(this)
}