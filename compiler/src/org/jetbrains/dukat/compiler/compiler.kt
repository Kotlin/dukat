package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.AstContext
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
import org.jetbrains.dukat.compiler.lowerings.lowerInheritance
import org.jetbrains.dukat.compiler.lowerings.lowerIntersectionType
import org.jetbrains.dukat.compiler.lowerings.lowerNativeArray
import org.jetbrains.dukat.compiler.lowerings.lowerNullable
import org.jetbrains.dukat.compiler.lowerings.lowerObjectLiterals
import org.jetbrains.dukat.compiler.lowerings.lowerOverrides
import org.jetbrains.dukat.compiler.lowerings.lowerVarargs

private fun ParameterValue.translateMeta(): String {

    meta?.asIntersection()?.let { parameterValue ->
        return " /* " + parameterValue.params.map { parameterValue ->
            if (parameterValue is TypeDeclaration) {
                parameterValue.value
            } else ""
        }.joinToString(" & ") + " */"
    }

    if (nullable) {
        return " /*= null*/"
    }

    return ""
}

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
            var paramSerialized = param.name + ": " + param.type.translate() + param.type.translateMeta()
            paramsList.add(paramSerialized)
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


    if (initializer != null) {
        if (initializer!!.kind.value == "@@DEFINED_EXTERNALLY") {
            res += " = definedExternally"

            initializer!!.meta?.let { meta ->
                res += " /* ${meta} */"
            }
        }
    } else {
        res += type.translateMeta()
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

private fun FunctionDeclaration.translate(): String {
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

    val overrideClause = if (override) "override" else "open"

    return listOf(
            annotation,
            "${overrideClause}${operatorModifier} fun${typeParams} ${name}(${translateParameters(parameters)}): ${returnType} = definedExternally"
    ).filterNotNull()
}

private fun VariableDeclaration.translate(): String {
    return "external var ${name}: ${type.translate()}${type.translateMeta()} = definedExternally"
}

private fun PropertyDeclaration.translate(parent: ClassDeclaration? = null): String {
    val modifier = if (override) "override" else "open"
    return "${modifier} var ${name}: ${type.translate()}${type.translateMeta()} = definedExternally"
}

private fun MemberDeclaration.translate(parent: ClassDeclaration? = null): List<String> {
    if (this is MethodDeclaration) {
        return translate(parent)
    } else if (this is PropertyDeclaration) {
        return listOf(translate(parent))
    } else {
        throw Exception("can not translate ${this}")
    }
}


private fun VariableDeclaration.translateSignature() = "var ${this.name}: ${this.type.translate()}"
private fun PropertyDeclaration.translateSignature(): String {
    val varModifier = if (getter && !setter) "val" else "var"
    val overrideClause = if (override) "override " else ""

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }
    var res = "${overrideClause}${varModifier}${typeParams} ${this.name}: ${this.type.translate()}"
    if (getter) {
        res += " get() = definedExternally"
    }
    if (setter) {
        res += "; set(value) = definedExternally"
    }
    return res
}

private fun MethodDeclaration.translateSignature(): List<String> {
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
    val overrideClause = if (override) "override " else ""

    return listOf(
            annotation,
            "${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters)})${returnClause}"
    ).filterNotNull()
}

private fun MemberDeclaration.translateSignature(): List<String> {
    if (this is MethodDeclaration) {
        return translateSignature()
    } else if (this is PropertyDeclaration) {
        return listOf(translateSignature())
    } else {
        throw Exception("can not translate singature ${this}")
    }
}

private fun DocumentRoot.updateContext(astContext: AstContext) : DocumentRoot {
    for (declaration in declarations) {
        if (declaration is InterfaceDeclaration) {
            astContext.registerInterface(declaration)
        }
        if (declaration is ClassDeclaration) {
            astContext.registerClass(declaration)
        }
    }

    return this
}

private fun unquote(name: String): String {
    return name.replace("(?:^\")|(?:\"$)".toRegex(), "")
}

private fun escapePackageName(name: String): String {
    return name.replace("/".toRegex(), ".").replace("_".toRegex(), "_")
}

fun compile(documentRoot: DocumentRoot, parent: DocumentRoot? = null, astContext: AstContext? = null): String {
    val myAstContext = astContext ?: AstContext()

    val docRoot = documentRoot
            .lowerObjectLiterals()
            .lowerConstructors()
            .lowerNativeArray()
            .lowerNullable()
            .lowerPrimitives()
            .lowerVarargs()
            .lowerIntersectionType()
            .updateContext(myAstContext)
            .lowerInheritance(myAstContext)
            .lowerOverrides()


    if (documentRoot.declarations.isEmpty()) {
        return "// NO DECLARATIONS"
    }


    val res = mutableListOf<String>()
    var packageName = docRoot.packageName
    if (parent != null) {
        val unquotedPackageName = unquote(packageName)
        res.add("@file:JsQualifier(\"$unquotedPackageName\")")
        packageName = "${parent.packageName}.${escapePackageName(unquotedPackageName)}"
        res.add("package " + packageName)
        res.add("")
    } else {
        if (docRoot.declarations[0] !is DocumentRoot) {
            res.add("package " + packageName)
            res.add("")
        }
    }

    for (declaration in docRoot.declarations) {
        if (declaration is DocumentRoot) {
            if (res.isNotEmpty()) {
                res.add("")
                res.add("// ------------------------------------------------------------------------------------------")
            }
            res.add(compile(declaration, docRoot, myAstContext))
        } else if (declaration is VariableDeclaration) {
            res.add(declaration.translate())
        } else if (declaration is FunctionDeclaration) {
            res.add(declaration.translate())
        } else if (declaration is ClassDeclaration) {
            val primaryConstructor = declaration.primaryConstructor

            val parents = if (declaration.parentEntities.isNotEmpty()) {
                " : " + declaration.parentEntities.map { parentEntity ->
                    "${parentEntity.name}${translateTypeParameters(parentEntity.typeParameters)}"
                }.joinToString(", ")
            } else ""

            val classDeclaration = "external open class ${declaration.name}${translateTypeParameters(declaration.typeParameters)}${parents}"
            val params = if (primaryConstructor == null) "" else
                if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

            val hasMembers = declaration.members.isNotEmpty()
            res.add(classDeclaration + params + if (hasMembers) " {" else "")

            val members = declaration.members
            if (hasMembers) {
                res.addAll(members.flatMap { it.translate(declaration) }.map({ "    " + it }))
                res.add("}")
            }

        } else if (declaration is InterfaceDeclaration) {
            val hasMembers = declaration.members.isNotEmpty()
            val parents = if (declaration.parentEntities.isNotEmpty()) {
                " : " + declaration.parentEntities.map { parentEntity ->
                    "${parentEntity.name}${translateTypeParameters(parentEntity.typeParameters)}"
                }.joinToString(", ")
            } else ""
            res.add("external interface ${declaration.name}${translateTypeParameters(declaration.typeParameters)}${parents}" + if (hasMembers) " {" else "")

            if (hasMembers) {
                res.addAll(declaration.members.flatMap { it.translateSignature() }.map { "    " + it })
                res.add("}")
            }

        }
    }

    return res.joinToString("\n")
}

fun compile(fileName: String, translator: Translator): String {
    return compile(translator.translateFile(fileName))
}