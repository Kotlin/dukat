package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.isGeneric
import org.jetbrains.dukat.compiler.lowerings.lowerNullable

fun translateType(declaration: TypeDeclaration): String {
    val res = mutableListOf<String>(declaration.value)
    if (declaration.isGeneric()) {
        val paramsList = mutableListOf<String>()
        for (param in declaration.params) {
            paramsList.add(translateType(param))
        }
        res.add("<" + paramsList.joinToString(", ") + ">")
    }
    return res.joinToString("")
}

fun lowerNativeArray(node: DocumentRoot): DocumentRoot {

    val loweredDeclarations = node.declarations.map { declaration ->
        when (declaration) {
            is VariableDeclaration -> {
                if (declaration.type.value == "@@ArraySugar") {
                    VariableDeclaration(declaration.name, TypeDeclaration("Array", declaration.type.params))
                } else {
                    declaration.copy()
                }
            }
            else -> declaration.duplicate() as Declaration
        }
    }


    return node.copy(declarations = loweredDeclarations)
}



private fun ParameterDeclaration.toStringRepresentation(): String {
    var res = name + ": " + translateType(type)

    initializer?.let {
        if (it.kind.value == "@@DEFINED_EXTERNALLY") {
            res += " = definedExternally"

            it.meta?.let { meta ->
                res += " /* ${meta} */"
            }

        }
    }

    return res
}

fun compile(documentRoot: DocumentRoot): String {
    var docRoot = lowerNativeArray(documentRoot)
    docRoot = lowerNullable(docRoot)
    docRoot = lowerPrimitives(docRoot)

    val res = mutableListOf<String>()

    for (child in docRoot.declarations) {
        if (child is VariableDeclaration) {
            val declaration = child
            res.add("export var ${declaration.name}: ${translateType(declaration.type)}")
        } else if (child is FunctionDeclaration) {
            val declaration = child
            val params = declaration.parameters
                    .map(ParameterDeclaration::toStringRepresentation)
                    .joinToString(", ")
            val returnType = translateType(child.type)
            res.add("external fun ${declaration.name}(${params}): ${returnType} = definedExternally")
        }
    }

    return res.joinToString("\n")
}

fun compile(fileName: String, translator: Translator): String {
    return compile(translator.translateFile(fileName))
}