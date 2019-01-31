package org.jetbrains.dukat.compiler.visitor

import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
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