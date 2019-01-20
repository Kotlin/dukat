package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.extended.ObjectLiteral

private class LowerObjectLiterals : ParameterValueLowering() {

    private val myGeneratedInterfaces = mutableListOf<InterfaceDeclaration>()

    private fun generateInterface(objectLiteral: ObjectLiteral): InterfaceDeclaration {
        val interfaceDeclaration = InterfaceDeclaration("`T$${myGeneratedInterfaces.size}`", objectLiteral.members, emptyList(), emptyList())
        myGeneratedInterfaces.add(interfaceDeclaration)
        return interfaceDeclaration
    }

    override fun lowerParameterValue(declaration: ParameterValue): ParameterValue {
        return if (declaration is ObjectLiteral) {

            val generatedInterface = generateInterface(declaration)

            TypeDeclaration(generatedInterface.name, emptyList())
        } else super.lowerParameterValue(declaration)
    }

    override fun lower(documenRoot: DocumentRoot): DocumentRoot {
        return documenRoot.copy(declarations = listOf(myGeneratedInterfaces, lowerDeclarations(documenRoot.declarations)).flatten())
    }
}

fun DocumentRoot.lowerObjectLiterals(): DocumentRoot {
    return LowerObjectLiterals().lower(this)
}