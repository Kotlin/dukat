package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.isGeneric
import org.jetbrains.dukat.compiler.lowerings.lowerConstructors
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs

private fun ParameterValue.translate() : String {
    if (this is TypeDeclaration) {
        val res = mutableListOf(value)
        if (isGeneric()) {
            val paramsList = mutableListOf<String>()
            for (param in params) {
                paramsList.add(param.translate())
            }
            res.add("<" + paramsList.joinToString(", ") + ">")
        }
        if (nullable) {
            res.add("?")
        }
        return res.joinToString("")
    } else if (this is FunctionTypeDeclaration) {
        val res = mutableListOf("(")
        val paramsList = mutableListOf<String>()
        for (param in parameters) {
            var paramSerializerd = param.name + ": " + param.type.translate()
            if (param.type.nullable) {
                paramSerializerd += " /*= null*/"
            }
            paramsList.add(paramSerializerd)
        }
        res.add(paramsList.joinToString(", ") + ")")
        res.add(" -> ${type.translate()}")
        var translated = res.joinToString("")
        if (nullable) {
            translated = "(${translated})?"
        }
        return translated
    } else {
        throw Exception("failed to translateType ${this}")
    }
}



private fun ParameterDeclaration.translate(): String {
    var res = name + ": " + type.translate()
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

private fun translateTypeParameters(typeParameters: List<TypeParameter>) : String {
    if (typeParameters.isEmpty()) {
        return ""
    } else {
        return "<" + typeParameters.map { typeParameter ->
            val constraintDescription = if (typeParameter.constraints.isEmpty()) {
                ""
            } else {
                " : ${typeParameter.constraints[0].translate()}"
            }
            typeParameter.name + constraintDescription
        }.joinToString(", ") + ">"
    }
}

private fun translateParameters(parameters: List<ParameterDeclaration>) : String {
    return parameters
            .map(ParameterDeclaration::translate)
            .joinToString(", ")
}

private fun FunctionDeclaration.translate(parent: ClassDeclaration? = null): String {
    val returnType = type.translate()

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val modifier  = if (parent != null) { "open" } else { "external" }
    return ("${modifier} fun${typeParams} ${name}(${translateParameters(parameters)}): ${returnType} = definedExternally")
}

private fun VariableDeclaration.translate(parent: ClassDeclaration? = null) : String {
    val modifier  = if (parent != null) { "open" } else { "external" }
    return "${modifier} var ${name}: ${type.translate()} = definedExternally"
}

private fun MemberDeclaration.translate(parent: ClassDeclaration? = null) : String {
    if (this is FunctionDeclaration) {
        return translate(parent)
    } else if (this is VariableDeclaration) {
        return translate(parent)
    } else {
        throw Exception("can not translate ${this}")
    }
}

fun compile(documentRoot: DocumentRoot): String {
    val docRoot = documentRoot
            .lowerConstructors()
            .lowerNativeArray()
            .lowerNullable()
            .lowerPrimitives()
            .lowerVarargs()

    val res = mutableListOf<String>()

    for (child in docRoot.declarations) {
        if (child is VariableDeclaration) {
            res.add(child.translate())
        } else if (child is FunctionDeclaration) {
            res.add(child.translate())
        } else if (child is ClassDeclaration) {
            var primaryConstructor: FunctionDeclaration? = child.primaryConstructor

            val classDeclaration = "external open class ${child.name}${translateTypeParameters(child.typeParameters)}"
            val params = if (primaryConstructor == null) "" else
                if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

            res.add(classDeclaration + params)

            val members = child.members
            if (members.isNotEmpty()) {
                res[res.size - 1] += " {"
                res.addAll(members.map { "    " + it.translate(child) })
                res.add("}")
            }
        } else if (child is InterfaceDeclaration) {
            res.add("external interface ${child.name}")
        }
    }

    return res.joinToString("\n")
}

fun compile(fileName: String, translator: Translator): String {
    return compile(translator.translateFile(fileName))
}