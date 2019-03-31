package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.AssignmentStatementNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementRightNode
import org.jetbrains.dukat.ast.model.nodes.ReturnStatement
import org.jetbrains.dukat.ast.model.nodes.StatementCallNode
import org.jetbrains.dukat.ast.model.nodes.StatementNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.DelegationModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.isGeneric
import org.jetbrains.dukat.translator.ModelVisitor


private fun String?.translateMeta(): String {
    return if (this != null) {
        if (this.startsWith("=")) {
            " /*${this}*/"
        } else {
            " /* ${this} */"
        }
    } else {
        ""
    }
}

private fun TypeNode.translateMeta(): String {
    return when (this) {
        is TypeValueModel -> metaDescription.translateMeta()
        is FunctionTypeModel -> metaDescription.translateMeta()
        else -> ""
    }
}

private fun translateTypeParams(params: List<TypeNode>): String {
    return "<" + params.joinToString(", ") { param -> "${param.translate()}${param.translateMeta()}" } + ">"
}

fun TypeNode.translate(needsMeta: Boolean = false): String {
    return when (this) {
        is TypeValueModel -> {
            val res = mutableListOf(value.translate())
            if (isGeneric()) {
                res.add(translateTypeParams(params))
            }
            if (nullable) {
                res.add("?")
            }
            res.joinToString("")
        }
        is FunctionTypeModel -> {
            val res = mutableListOf("(")
            val paramsList = mutableListOf<String>()
            for (param in parameters) {
                val paramSerialized = param.name + ": " + param.type.translate() + param.type.translateMeta()
                paramsList.add(paramSerialized)
            }
            res.add(paramsList.joinToString(", ") + ")")
            res.add(" -> ${type.translate()}")
            var translated = res.joinToString("")
            if (nullable) {
                translated = "(${translated})?"
            }
            translated
        }
        is QualifiedNode -> return translate()
        else -> return "failed to translateType ${this}"
    }
}

private fun ParameterModel.translate(needsMeta: Boolean = true): String {
    var res = name + ": " + type.translate(needsMeta)
    if (vararg) {
        res = "vararg $res"
    }


    if (initializer != null) {
        if (needsMeta) {

            if (initializer?.kind?.value != null) {
                res += " = ${initializer?.kind?.value}"

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

private fun translateTypeParameters(typeParameters: List<TypeParameterModel>): String {
    return if (typeParameters.isEmpty()) {
        ""
    } else {
        "<" + typeParameters.map { typeParameter ->
            val constraintDescription = if (typeParameter.constraints.isEmpty()) {
                ""
            } else {
                " : ${typeParameter.constraints[0].translate()}"
            }
            typeParameter.name + constraintDescription
        }.joinToString(", ") + ">"
    }
}

private fun translateTypeArguments(typeParameters: List<IdentifierNode>): String {
    return if (typeParameters.isEmpty()) {
        ""
    } else {
        "<" + typeParameters.map { it.value }.joinToString(", ") + ">"
    }
}


private fun translateParameters(parameters: List<ParameterModel>, needsMeta: Boolean = true): String {
    return parameters
            .map { parameter -> parameter.translate(needsMeta) }
            .joinToString(", ")
}

private fun translateAnnotations(annotations: List<AnnotationNode>): String {
    val annotations = annotations.map { annotationNode ->
        var res = "@" + annotationNode.name
        if (annotationNode.params.isNotEmpty()) {
            res = res + "(" + annotationNode.params.joinToString(", ") { "\"${it.translate()}\"" } + ")"
        }
        res
    }

    val annotationTranslated = if (annotations.isEmpty()) "" else annotations.joinToString("\n") + "\n"

    return annotationTranslated
}

private fun QualifiedStatementLeftNode.translate(): String {
    return when (this) {
        is IdentifierNode -> value
        is StatementCallNode -> translate()
        is QualifiedStatementNode -> "${left.translate()}.${right.translate()}"
        else -> throw Exception("unkown QualifiedStatementLeftNode ${this}")
    }
}

private fun QualifiedStatementRightNode.translate(): String {
    return when (this) {
        is IdentifierNode -> value
        is StatementCallNode -> translate()
        else -> throw Exception("unkown QualifiedStatementRightNode ${this}")
    }
}

private fun StatementCallNode.translate(): String {
    return "${value}(${params.joinToString(", ") { it.value }})"
}

private fun StatementNode.translate(): String {
    return when (this) {
        is QualifiedStatementNode -> "${left.translate()}.${right.translate()}"
        is ReturnStatement -> "return ${statement.translate()}"
        is AssignmentStatementNode -> "${left.translate()} = ${right.translate()}"
        is IdentifierNode -> translate()
        else -> throw Exception("unkown StatementNode ${this}")
    }
}

private fun FunctionModel.translate(): String {
    val returnType = type.translate()

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val modifier = if (inline) "inline" else "external"
    val operator = if (operator) " operator" else ""

    val body = if (body.isEmpty()) {
        "= definedExternally"
    } else {
        "{ ${body.joinToString { statementNode -> statementNode.translate() }} }"
    }
    return "${translateAnnotations(annotations)}${modifier}${operator} fun${typeParams} ${name.translate()}(${translateParameters(parameters)}): ${returnType}${type.translateMeta()} ${body}"
}

private fun MethodModel.translate(): List<String> {
    val returnsUnit = (type is TypeValueModel) && ((type as TypeValueModel).value == IdentifierNode("@@None"))
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) "operator " else ""
    val annotations = annotations.map { "@${it.name}" }

    val open = !static && open
    val overrideClause = if (override) "override " else if (open) "open " else ""

    val definedExternallyClause = if (definedExternally) " = definedExternally" else ""

    val metaClause = type.translateMeta()
    return annotations + listOf("${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters, !override)})${returnClause}$metaClause${definedExternallyClause}")
}

private fun ConstructorModel.translate(): List<String> {
    val typeParams = translateTypeParameters(typeParameters)
    return listOf("constructor${typeParams}(${translateParameters(parameters, false)})")
}

private fun VariableModel.translate(): String {
    val variableKeyword = if (immutable) "val" else "var"
    val modifier = if (inline) "inline" else "external"
    val getter = "get() = ${get?.translate()};"
    val setter = "set(value) { ${set?.translate()} }"

    val body = if (initializer != null) {
        "= ${initializer?.translate()}"
    } else {
        "${getter} ${setter}"
    }

    val typeParams = if (typeParameters.isEmpty()) {
        ""
    } else {
        " ${translateTypeParameters(typeParameters)}"
    }

    return "${translateAnnotations(annotations)}${modifier} ${variableKeyword}${typeParams} ${name.translate()}: ${type.translate()}${type.translateMeta()} ${body}"
}

private fun EnumNode.translate(): String {
    val res = mutableListOf("external enum class ${name} {")
    res.add(values.map { value ->
        val metaClause = if (value.meta.isEmpty()) "" else " /* = ${value.meta} */"
        "    ${value.value}${metaClause}"
    }.joinToString(",\n"))
    res.add("}")
    return res.joinToString("\n")
}

private fun PropertyModel.translate(): String {
    val open = !static && open
    val modifier = if (override) "override " else if (open) "open " else ""

    val definedExternallyClause = if (definedExternally) " = definedExternally" else ""
    return "${modifier}var ${name}: ${type.translate()}${type.translateMeta()}${definedExternallyClause}"
}

private fun MemberNode.translate(): List<String> {
    return when (this) {
        is MethodModel -> translate()
        is PropertyModel -> listOf(translate())
        is ConstructorModel -> translate()
        is ClassModel -> listOf(translate(true, 1))
        else -> throw Exception("can not translate ${this}")
    }
}

private fun PropertyModel.translateSignature(): String {
    val varModifier = if (getter && !setter) "val" else "var"
    val overrideClause = if (override) "override " else ""


    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }
    val metaClause = type.translateMeta()
    var res = "${overrideClause}${varModifier}${typeParams} ${this.name}: ${type.translate()}${metaClause}"
    if (getter) {
        res += " get() = definedExternally"
    }
    if (setter) {
        res += "; set(value) = definedExternally"
    }
    return res
}

private fun MethodModel.translateSignature(): List<String> {
    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) "operator " else ""
    val annotations = annotations.map { "@${it.name}" }

    val returnsUnit = (type is TypeValueModel) && ((type as TypeValueModel).value == IdentifierNode("Unit"))
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"
    val overrideClause = if (override) "override " else ""

    val metaClause = type.translateMeta()
    val methodNodeTranslation = "${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters)})${returnClause}$metaClause"
    return annotations + listOf(methodNodeTranslation)
}

private fun MemberNode.translateSignature(): List<String> {
    return when (this) {
        is MethodModel -> translateSignature()
        is PropertyModel -> listOf(translateSignature())
        else -> throw Exception("can not translate singature ${this}")
    }
}

private fun HeritageSymbolNode.translate(): String {
    return when (this) {
        is IdentifierNode -> translate()
        is PropertyAccessNode -> expression.translate() + "." + name.translate()
        else -> throw Exception("unknown heritage clause ${this}")
    }
}

private fun translateHeritageNodes(parentEntities: List<HeritageNode>): String {
    val parents = if (parentEntities.isNotEmpty()) {
        " : " + parentEntities.map { parentEntity ->
            "${parentEntity.name.translate()}${translateTypeArguments(parentEntity.typeArguments)}"
        }.joinToString(", ")
    } else ""

    return parents
}

private fun TypeNode.translateAsHeritageClause(): String {
    return when (this) {
        is FunctionTypeModel -> translate()
        is TypeValueModel -> {
            val typeParams = if (params.isEmpty()) {
                ""
            } else {
                "<${params.joinToString("::") { it.translateAsHeritageClause() }}>"
            }

            when (value) {
                is IdentifierNode -> "${(value as IdentifierNode).value}${typeParams}"
                else -> throw Exception("unknown ValueTypeNodeValue ${value}")
            }
        }
        else -> ""
    }
}

private fun DelegationModel.translate(): String {
    return when (this) {
        is ClassModel -> name
        is ExternalDelegationModel -> "definedExternally"
        else -> ""
    }
}

private fun HeritageModel.translateAsHeritageClause(): String {
    val delegationClause = delegateTo?.let { " by ${it.translate()}" } ?: ""
    return "${value.translateAsHeritageClause()}${delegationClause}"
}


private fun ClassModel.translate(nested: Boolean, padding: Int): String {
    val res: MutableList<String> = kotlin.collections.mutableListOf()
    val primaryConstructor = primaryConstructor

    val parents = translateHeritageNodes(parentEntities)
    val externalClause = if (nested) "" else "external "
    val params = if (primaryConstructor == null) "" else
        if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

    val classDeclaration = "${translateAnnotations(annotations)}${externalClause}open class ${name}${translateTypeParameters(typeParameters)}${params}${parents}"

    val members = members
    val staticMembers = companionObject.members

    val hasMembers = members.isNotEmpty()
    val hasStaticMembers = staticMembers.isNotEmpty()
    val isBlock = hasMembers || hasStaticMembers

    val tab = "    "

    res.add(classDeclaration + if (isBlock) " {" else "")

    if (hasMembers) {
        res.addAll(members.flatMap { it.translate() }.map({ tab.repeat(padding + 1) + it }))
    }

    if (staticMembers.isNotEmpty()) {
        res.add(tab.repeat(padding + 1) + "companion object {")
        res.addAll(staticMembers.flatMap { it.translate() }.map({ tab.repeat(padding + 2) + it }))
        res.add(tab.repeat(padding + 1) + "}")
    }


    if (isBlock) {
        res.add(tab.repeat(padding) + "}")
    }

    return res.joinToString("\n")
}


class StringTranslator : ModelVisitor {
    private var myOutput: MutableList<String> = mutableListOf()

    private fun addOutput(fragment: String) {
        myOutput.add(fragment)
    }

    fun output(): String {
        return myOutput.joinToString("\n")
    }

    override fun visitVariable(variable: VariableModel) {
        addOutput(variable.translate())
    }

    override fun visitFunction(function: FunctionModel) {
        addOutput(function.translate())
    }

    override fun visitObject(objectNode: ObjectModel) {
        val objectModel = "external object ${objectNode.name}"

        val members = objectNode.members

        val hasMembers = members.isNotEmpty()

        addOutput(objectModel + " {")

        if (hasMembers) {
            members.flatMap { it.translate() }.map({ "    " + it }).forEach {
                addOutput(it)
            }

        }

        addOutput("}")
    }

    override fun visitEnum(enumNode: EnumNode) {
        addOutput(enumNode.translate())
    }

    override fun visitInterface(interfaceModel: InterfaceModel) {
        val hasMembers = interfaceModel.members.isNotEmpty()
        val staticMembers = interfaceModel.companionObject.members

        val showCompanionObject = staticMembers.isNotEmpty() || interfaceModel.companionObject.parentEntities.isNotEmpty()

        val isBlock = hasMembers || staticMembers.isNotEmpty() || showCompanionObject
        val parents = translateHeritageNodes(interfaceModel.parentEntities)

        addOutput("${translateAnnotations(interfaceModel.annotations)}external interface ${interfaceModel.name}${translateTypeParameters(interfaceModel.typeParameters)}${parents}" + if (isBlock) " {" else "")
        if (isBlock) {
            interfaceModel.members.flatMap { it.translateSignature() }.map { "    " + it }.forEach { addOutput(it) }

            val parents = if (interfaceModel.companionObject.parentEntities.isEmpty()) {
                ""
            } else {
                " : ${interfaceModel.companionObject.parentEntities.map { it.translateAsHeritageClause() }.joinToString(", ")}"
            }

            if (showCompanionObject) {
                addOutput("    companion object${parents} {")

                staticMembers.flatMap { it.translate() }.map({ "        ${it}" }).forEach { addOutput(it) }
                addOutput("    }")
            }

            addOutput("}")
        }

    }

    override fun visitClass(classModel: ClassModel) {
        addOutput(classModel.translate(false, 0))
    }

    override fun visitModule(moduleModel: ModuleModel) {
        if (moduleModel.declarations.isEmpty() && moduleModel.sumbodules.isEmpty()) {
            return
        }

        val containsSomethingExceptDocRoot = moduleModel.declarations.isNotEmpty()

        if (containsSomethingExceptDocRoot) {
            addOutput("${translateAnnotations(moduleModel.annotations)}package ${moduleModel.packageName}")
            addOutput("")
        }
    }

}