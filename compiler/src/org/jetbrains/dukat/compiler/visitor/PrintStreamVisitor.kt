package org.jetbrains.dukat.compiler.visitor

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class PrintStreamVisitor(): Visitor {
    private val myOutputStream = ByteArrayOutputStream()
    private val myPrintStream = PrintStream(myOutputStream)

    override fun visitFunctionDeclaration(declaration: FunctionDeclaration) {
        myPrintStream.print("external fun ${declaration.name}(")
        myPrintStream.print("): ")
        visitParameterValue(declaration.type)
        myPrintStream.println()
    }

    override fun visitParameterValue(declaration: ParameterValue) {
        if (declaration is TypeDeclaration) {
            myPrintStream.print(declaration.value)
        } else if (declaration is FunctionTypeDeclaration) {
            //myPrintStream.print()
        }
    }

    fun output(document: DocumentRoot): String {
        visitDocumentRoot(document)
        return String(myOutputStream.toByteArray(), Charsets.UTF_8)
    }
}