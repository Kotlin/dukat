package org.jetbrains.dukat.compiler.visitor

import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class PrintStreamVisitor(): DeclarationVisitor {
    private val myOutputStream = ByteArrayOutputStream()
    private val myPrintStream = PrintStream(myOutputStream)

    override fun visitFunctionDeclaration(declaration: FunctionDeclaration) {
        myPrintStream.print("external fun ${declaration.name}(")
        myPrintStream.print("): ")
        visitParameterValue(declaration.type)
        myPrintStream.println()
    }

    override fun visitParameterValue(declaration: ParameterValueDeclaration) {
        if (declaration is TypeDeclaration) {
            myPrintStream.print(declaration.value)
        } else if (declaration is FunctionTypeDeclaration) {
            //myPrintStream.print()
        }
    }

    fun output(document: DocumentRootDeclaration): String {
        visitDocumentRoot(document)
        return String(myOutputStream.toByteArray(), Charsets.UTF_8)
    }
}