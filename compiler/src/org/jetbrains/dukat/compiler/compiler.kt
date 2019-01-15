package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.isGeneric
import org.jetbrains.dukat.compiler.lowerings.lowerConstructors
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs

private fun ParameterValue.translate(): String {
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

private fun translateTypeParameters(typeParameters: List<TypeParameter>): String {
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

private fun translateParameters(parameters: List<ParameterDeclaration>): String {
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

    return ("external fun${typeParams} ${name}(${translateParameters(parameters)}): ${returnType} = definedExternally")
}

private fun MethodDeclaration.translate(parent: ClassDeclaration? = null): List<String> {
    val returnType = type.translate()

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) " operator" else ""
    val annotation = if (operator) {
        if (name == "set") {
            "@nativeSetter"
        } else if (name == "get") {
            "@nativeGetter"
        } else null
    } else null
    return listOf(
            annotation,
            "open${operatorModifier} fun${typeParams} ${name}(${translateParameters(parameters)}): ${returnType} = definedExternally"
    ).filterNotNull()
}

private fun VariableDeclaration.translate(parent: ClassDeclaration? = null): String {
    val modifier = if (parent != null) {
        "open"
    } else {
        "external"
    }
    return "${modifier} var ${name}: ${type.translate()} = definedExternally"
}

private fun MemberDeclaration.translate(parent: ClassDeclaration? = null): List<String> {
    if (this is MethodDeclaration) {
        return translate(parent)
    } else if (this is VariableDeclaration) {
        return listOf(translate(parent))
    } else {
        throw Exception("can not translate ${this}")
    }
}


private fun VariableDeclaration.translateSignature() = "var ${this.name}: ${this.type.translate()}"
private fun PropertyDeclaration.translateSignature(): String {
    val varModifier = if (getter && !setter) "val" else "var"
    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }
    var res =  "${varModifier}${typeParams} ${this.name}: ${this.type.translate()}"
    if (getter) {
        res += " get() = definedExternally"
    }
    if (setter) {
        res += "; set(value) = definedExternally"
    }
    return res
}

private fun MethodDeclaration.translateSignature() : List<String> {
    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) "operator " else ""
    val annotation = if (operator) {
        if (name == "set") {
            "@nativeSetter"
        } else if (name == "get") {
            "@nativeGetter"
        } else if (name == "invoke") {
            "@nativeInvoke"
        } else null
    } else null

    val returnsUnit = type == TypeDeclaration("Unit", emptyArray())
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"


    return listOf(
            annotation,
            "${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters)})${returnClause}"
    ).filterNotNull()
}

private fun MemberDeclaration.translateSignature(): List<String> {
    if (this is VariableDeclaration) {
        return listOf(translateSignature())
    } else if (this is MethodDeclaration) {
        return translateSignature()
    } else if (this is PropertyDeclaration) {
        return listOf(translateSignature())
    } else {
        throw Exception("can not translate singature ${this}")
    }
}

fun compile(documentRoot: DocumentRoot): String {
    val docRoot = documentRoot
            .lowerConstructors()
            .lowerNativeArray()
            .lowerNullable()
            .lowerPrimitives()
            .lowerVarargs()


    if (documentRoot.declarations.isEmpty()) {
        return "// NO DECLARATIONS"
    }


    val res = mutableListOf<String>()
    res.add("package " + docRoot.packageName)
    res.add("")

    for (child in docRoot.declarations) {
        if (child is VariableDeclaration) {
            res.add(child.translate())
        } else if (child is FunctionDeclaration) {
            res.add(child.translate())
        } else if (child is ClassDeclaration) {
            val primaryConstructor = child.primaryConstructor

            val classDeclaration = "external open class ${child.name}${translateTypeParameters(child.typeParameters)}"
            val params = if (primaryConstructor == null) "" else
                if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

            val hasMembers = child.members.isNotEmpty()
            res.add(classDeclaration + params + if (hasMembers) " {" else "")

            val members = child.members
            if (hasMembers) {
                res.addAll(members.flatMap { it.translate(child) }.map({ "    " + it }))
                res.add("}")
            }

        } else if (child is InterfaceDeclaration) {
            val hasMembers = child.members.isNotEmpty()
            val parents = if (child.parentEntities.isNotEmpty()) {
                " : " + child.parentEntities.map {
                    parentEntity -> "${parentEntity.name}${translateTypeParameters(parentEntity.typeParameters)}"
                }.joinToString(", ")
            } else ""
            res.add("external interface ${child.name}${translateTypeParameters(child.typeParameters)}${parents}" + if (hasMembers) " {" else "")

            if (hasMembers) {
                res.addAll(child.members.flatMap { it.translateSignature() }.map { "    " + it })
                res.add("}")
            }

        }
    }

    return res.joinToString("\n")
}

fun compile(fileName: String, translator: Translator): String {
    return compile(translator.translateFile(fileName))
}