package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.AstContext
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TokenDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.StringTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.isGeneric
import org.jetbrains.dukat.ast.model.nodes.DynamicTypeNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.compiler.translator.InputTranslator
import org.jetbrains.dukat.compiler.visitor.PrintStreamVisitor

private fun ParameterValueDeclaration.translateMeta(): String {

    meta?.asIntersection()?.let { parameterValue ->
        return " /* " + parameterValue.params.map { parameterValue ->
            if (parameterValue is TypeDeclaration) {
                parameterValue.value
            } else ""
        }.joinToString(" & ") + " */"
    }

    val skipNullableAnnotation = (this is TypeDeclaration) && (this.value == "Nothing")
    if (nullable && !skipNullableAnnotation) {
        //TODO: consider rethinking this restriction
        return " /*= null*/"
    }

    if (meta is StringTypeDeclaration) {
        (meta as StringTypeDeclaration)?.let {
            return " /* ${it.tokens.joinToString("|")} */"
        }
    }

    return ""
}

private fun ParameterValueDeclaration.translateSignatureMeta(): String {

    meta?.asSelfReference()?.let {
        return " /* this */"
    }

    return ""
}

private fun ParameterValueDeclaration.translate(): String {
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
    } else if (this is DynamicTypeNode) {
        return "dynamic"
    } else {
        return "failed to translateType ${this}"
        //throw Exception("failed to translateType ${this}")
    }
}


private fun ParameterDeclaration.translate(needsMeta: Boolean = true): String {
    var res = name + ": " + type.translate()
    if (vararg) {
        res = "vararg $res"
    }


    if (initializer != null) {
        if (needsMeta) {

            if (initializer!!.kind.value == "@@DEFINED_EXTERNALLY") {
                res += " = definedExternally"

                initializer!!.meta?.let { meta ->
                    res += " /* ${meta} */"
                }
            }
        }
    } else {
        res += type.translateMeta()
    }

    return res
}

private fun translateTypeParameters(typeParameters: List<TypeParameterDeclaration>): String {
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

private fun translateTypeArguments(typeParameters: List<TokenDeclaration>): String {
    if (typeParameters.isEmpty()) {
        return ""
    } else {
        return "<" + typeParameters.map{it.value}.joinToString(", ") + ">"
    }
}


private fun translateParameters(parameters: List<ParameterDeclaration>, needsMeta: Boolean = true): String {
    return parameters
            .map { parameter -> parameter.translate(needsMeta) }
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

private fun MethodNode.translate(): List<String> {
    val returnType = type.translate()

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) " operator" else ""
    val annotations = annotations.map { "@${it.name}" }

    val overrideClause = if (override) "override" else if (!static) "open" else ""

    return annotations.toMutableList() + listOf("${overrideClause}${operatorModifier} fun${typeParams} ${name}(${translateParameters(parameters, !override)}): ${returnType} = definedExternally")
}

private fun VariableDeclaration.translate(): String {
    return "external var ${name}: ${type.translate()}${type.translateMeta()} = definedExternally"
}

private fun PropertyNode.translate(): String {
    val modifier = if (override) "override" else if (!static) "open" else ""
    return "${modifier} var ${name}: ${type.translate()}${type.translateMeta()} = definedExternally"
}

private fun MemberDeclaration.translate(isStatic: Boolean = false): List<String> {
    if (this is MethodNode) {
        return translate()
    } else if (this is PropertyNode) {
        return listOf(translate())
    } else {
        throw Exception("can not translate ${this}")
    }
}

private fun PropertyNode.translateSignature(): String {
    val varModifier = if (getter && !setter) "val" else "var"
    val overrideClause = if (override) "override " else ""


    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }
    var res = "${overrideClause}${varModifier}${typeParams} ${this.name}: ${type.translate()}${type.translateSignatureMeta()}"
    if (getter) {
        res += " get() = definedExternally"
    }
    if (setter) {
        res += "; set(value) = definedExternally"
    }
    return res
}

private fun MethodNode.translateSignature(): List<String> {
    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) "operator " else ""
    val annotations = annotations.map { "@${it.name}" }

    val returnsUnit = type == TypeDeclaration("Unit", emptyArray())
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"
    val overrideClause = if (override) "override " else ""

    val methodNodeTranslation = "${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters)})${returnClause}${type.translateSignatureMeta()}"
    return if (annotations.isEmpty()) {
        listOf(
                methodNodeTranslation
        )
    } else annotations.toMutableList() + listOf(methodNodeTranslation)



}

private fun MemberDeclaration.translateSignature(): List<String> {
    if (this is MethodNode) {
        return translateSignature()
    } else if (this is PropertyNode) {
        return listOf(translateSignature())
    } else {
        throw Exception("can not translate singature ${this}")
    }
}


private fun unquote(name: String): String {
    return name.replace("(?:^\")|(?:\"$)".toRegex(), "")
}

private fun escapePackageName(name: String): String {
    return name.replace("/".toRegex(), ".").replace("_".toRegex(), "_")
}

private fun MemberDeclaration.isStatic() = when(this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}


fun processDeclarations(docRoot: DocumentRootDeclaration, res: MutableList<String>, parentDeclaration: DocumentRootDeclaration? = null) {
    if (docRoot.declarations.isEmpty()) {
        return
    }

    val packageName = if (parentDeclaration == null) unquote(docRoot.packageName) else "${unquote(parentDeclaration.packageName)}.${unquote(docRoot.packageName)}"

    if (docRoot.declarations[0] !is DocumentRootDeclaration) {
        res.add("package " + escapePackageName(packageName))
        res.add("")
    }

    for (declaration in docRoot.declarations) {
        if (declaration is DocumentRootDeclaration) {
            if (res.isNotEmpty()) {
                res.add("")
                res.add("// ------------------------------------------------------------------------------------------")
            }
            val children = mutableListOf<String>()
            processDeclarations(declaration, children, docRoot)
            if (children.isNotEmpty()) {
                res.add("@file:JsQualifier(\"${unquote(declaration.packageName)}\")")
            }
            res.addAll(children)
        } else if (declaration is VariableDeclaration) {
            res.add(declaration.translate())
        } else if (declaration is FunctionDeclaration) {
            res.add(declaration.translate())
        } else if (declaration is ClassDeclaration) {
            val primaryConstructor = declaration.primaryConstructor

            val parents = if (declaration.parentEntities.isNotEmpty()) {
                " : " + declaration.parentEntities.map { parentEntity ->
                    "${parentEntity.name}${translateTypeArguments(parentEntity.typeArguments)}"
                }.joinToString(", ")
            } else ""

            val classDeclaration = "external open class ${declaration.name}${translateTypeParameters(declaration.typeParameters)}${parents}"
            val params = if (primaryConstructor == null) "" else
                if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

            val staticMembers = declaration.members.filter { it.isStatic() }
            val members = declaration.members.filter { !it.isStatic() }

            val hasMembers = members.isNotEmpty()
            val hasStaticMembers = staticMembers.isNotEmpty()
            val isBlock = hasMembers || hasStaticMembers

            res.add(classDeclaration + params + if (isBlock) " {" else "")

            if (staticMembers.isNotEmpty()) {
                res.add("    companion object {")
                res.addAll(staticMembers.flatMap { it.translate(true) }.map({ "       ${it}" }))
                res.add("    }")
            }

            if (hasMembers) {
                res.addAll(members.flatMap { it.translate() }.map({ "    " + it }))
            }

            if (isBlock) {
                res.add("}")
            }

        } else if (declaration is InterfaceDeclaration) {
            val hasMembers = declaration.members.isNotEmpty()
            val parents = if (declaration.parentEntities.isNotEmpty()) {
                " : " + declaration.parentEntities.map { parentEntity ->
                    "${parentEntity.name}${translateTypeArguments(parentEntity.typeArguments)}"
                }.joinToString(", ")
            } else ""
            res.add("external interface ${declaration.name}${translateTypeParameters(declaration.typeParameters)}${parents}" + if (hasMembers) " {" else "")

            if (hasMembers) {
                res.addAll(declaration.members.flatMap { it.translateSignature() }.map { "    " + it })
                res.add("}")
            }

        }
    }
}

fun compile(documentRoot: DocumentRootDeclaration, parent: DocumentRootDeclaration? = null, astContext: AstContext? = null): String {
    val res = mutableListOf<String>()
    processDeclarations(documentRoot, res)

    if (res.isEmpty()) {
        res.add("// NO DECLARATIONS")
    }
    return res.joinToString("\n")
}

fun output(fileName: String, translator: InputTranslator): String {
    val documentRoot =
            translator.lower(translator.translateFile(fileName))
    return compile(documentRoot)
}

fun outputWithVisitor(fileName: String, translator: InputTranslator): String {
    val documentRoot = translator.lower(translator.translateFile(fileName))
    return PrintStreamVisitor().output(documentRoot)
}