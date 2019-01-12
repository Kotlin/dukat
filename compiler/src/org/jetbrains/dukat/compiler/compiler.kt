package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.isGeneric
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs

fun translateType(declaration: ParameterValue): String {

    if (declaration is TypeDeclaration) {
        val res = mutableListOf(declaration.value)
        if (declaration.isGeneric()) {
            val paramsList = mutableListOf<String>()
            for (param in declaration.params) {
                paramsList.add(translateType(param))
            }
            res.add("<" + paramsList.joinToString(", ") + ">")
        }
        if (declaration.nullable) {
            res.add("?")
        }
        return res.joinToString("")
    } else if (declaration is FunctionTypeDeclaration) {
        val res = mutableListOf("(")
        val paramsList = mutableListOf<String>()
        for (param in declaration.parameters) {
            var paramSerializerd = param.name + ": " + translateType(param.type)
            if (param.type.nullable) {
                paramSerializerd += " /*= null*/"
            }
            paramsList.add(paramSerializerd)
        }
        res.add(paramsList.joinToString(", ") + ")")
        res.add(" -> ${translateType(declaration.type)}")
        var translated = res.joinToString("")
        if (declaration.nullable) {
            translated = "(${translated})?"
        }
        return translated
    } else {
        throw Exception("failed to translateType ${declaration}")
    }
}


private fun ParameterDeclaration.toStringRepresentation(): String {
    var res = name + ": " + translateType(type)
    if (type.vararg) {
        res = "vararg $res"
    }

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
    docRoot = lowerVarargs(docRoot)

    val res = mutableListOf<String>()

    for (child in docRoot.declarations) {
        if (child is VariableDeclaration) {
            val declaration = child
            res.add("external var ${declaration.name}: ${translateType(declaration.type)} = definedExternally")
        } else if (child is FunctionDeclaration) {
            val declaration = child
            val params = declaration.parameters
                    .map(ParameterDeclaration::toStringRepresentation)
                    .joinToString(", ")
            val returnType = translateType(child.type)

            val typeParams =
                    if (child.typeParameters.isEmpty()) {
                        ""
                    } else {
                        " <" + child.typeParameters.map { typeParameter ->
                            val constraintDescription = if (typeParameter.constraints.isEmpty()) {
                                ""
                            } else {
                                " : ${translateType(typeParameter.constraints[0])}"
                            }
                            typeParameter.name + constraintDescription
                        }.joinToString(", ") + ">"
                    }
            res.add("external fun${typeParams} ${declaration.name}(${params}): ${returnType} = definedExternally")
        }
    }

    return res.joinToString("\n")
}

fun compile(fileName: String, translator: Translator): String {
    return compile(translator.translateFile(fileName))
}