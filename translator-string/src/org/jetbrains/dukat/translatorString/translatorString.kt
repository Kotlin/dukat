import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.AssignmentStatementNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementRightNode
import org.jetbrains.dukat.ast.model.nodes.ReturnStatement
import org.jetbrains.dukat.ast.model.nodes.StatementCallNode
import org.jetbrains.dukat.ast.model.nodes.StatementNode
import org.jetbrains.dukat.ast.model.nodes.translate
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.DelegationModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.isGeneric
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

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

fun ParameterValueDeclaration.translateMeta(): String {
    return when (this) {
        is TypeValueModel -> metaDescription.translateMeta()
        is FunctionTypeModel -> metaDescription.translateMeta()
        else -> ""
    }
}

private fun translateTypeParams(params: List<ParameterValueDeclaration>): String {
    return "<" + params.joinToString(", ") { param -> "${param.translate()}${param.translateMeta()}" } + ">"
}

fun ParameterValueDeclaration.translate(needsMeta: Boolean = false): String {
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

fun ParameterDeclaration.translate(needsMeta: Boolean = true): String {
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

fun translateTypeParameters(typeParameters: List<TypeParameterDeclaration>): String {
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

fun translateTypeArguments(typeParameters: List<IdentifierNode>): String {
    if (typeParameters.isEmpty()) {
        return ""
    } else {
        return "<" + typeParameters.map { it.value }.joinToString(", ") + ">"
    }
}


fun translateParameters(parameters: List<ParameterDeclaration>, needsMeta: Boolean = true): String {
    return parameters
            .map { parameter -> parameter.translate(needsMeta) }
            .joinToString(", ")
}

fun translateAnnotations(annotations: List<AnnotationNode>): String {
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

fun QualifiedStatementLeftNode.translate(): String {
    return when (this) {
        is IdentifierNode -> value
        is StatementCallNode -> translate()
        is QualifiedStatementNode -> "${left.translate()}.${right.translate()}"
        else -> throw Exception("unkown QualifiedStatementLeftNode ${this}")
    }
}

fun QualifiedStatementRightNode.translate(): String {
    return when (this) {
        is IdentifierNode -> value
        is StatementCallNode -> translate()
        else -> throw Exception("unkown QualifiedStatementRightNode ${this}")
    }
}

fun StatementCallNode.translate(): String {
    return "${value}(${params.joinToString(", ") { it.value }})"
}

fun StatementNode.translate(): String {
    return when (this) {
        is QualifiedStatementNode -> "${left.translate()}.${right.translate()}"
        is ReturnStatement -> "return ${statement.translate()}"
        is AssignmentStatementNode -> "${left.translate()} = ${right.translate()}"
        is IdentifierNode -> translate()
        else -> throw Exception("unkown StatementNode ${this}")
    }
}

fun FunctionModel.translate(): String {
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

fun MethodNode.translate(): List<String> {
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

fun ConstructorNode.translate(): List<String> {
    val typeParams = translateTypeParameters(typeParameters)
    return listOf("constructor${typeParams}(${translateParameters(parameters, false)})")
}

fun VariableModel.translate(): String {
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

fun EnumNode.translate(): String {
    val res = mutableListOf("external enum class ${name} {")
    res.add(values.map { value ->
        val metaClause = if (value.meta.isEmpty()) "" else " /* = ${value.meta} */"
        "    ${value.value}${metaClause}"
    }.joinToString(",\n"))
    res.add("}")
    return res.joinToString("\n")
}

fun PropertyNode.translate(): String {
    val open = !static && open
    val modifier = if (override) "override " else if (open) "open " else ""

    val definedExternallyClause = if (definedExternally) " = definedExternally" else ""
    return "${modifier}var ${name}: ${type.translate()}${type.translateMeta()}${definedExternallyClause}"
}

fun MemberNode.translate(): List<String> {
    if (this is MethodNode) {
        return translate()
    } else if (this is PropertyNode) {
        return listOf(translate())
    } else if (this is ConstructorNode) {
        return translate()
    } else if (this is ClassModel) {
        return listOf(translate(true, 1))
    } else {
        throw Exception("can not translate ${this}")
    }
}

fun PropertyNode.translateSignature(): String {
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

fun MethodNode.translateSignature(): List<String> {
    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) "operator " else ""
    val annotations = annotations.map { "@${it.name}" }

    val returnsUnit = (type is TypeValueModel) && ((type as TypeValueModel).value == IdentifierNode("Unit"))
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"
    val overrideClause = if (override) "override " else ""

//    val metaClause = if (operator) "" else type.translateMeta()
    val metaClause = type.translateMeta()
    val methodNodeTranslation = "${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters)})${returnClause}$metaClause"
    return annotations + listOf(methodNodeTranslation)
}

fun MemberNode.translateSignature(): List<String> {
    if (this is MethodNode) {
        return translateSignature()
    } else if (this is PropertyNode) {
        return listOf(translateSignature())
    } else {
        throw Exception("can not translate singature ${this}")
    }
}

fun HeritageSymbolNode.translate(): String {
    return when (this) {
        is IdentifierNode -> translate()
        is PropertyAccessNode -> expression.translate() + "." + name.translate()
        else -> throw Exception("unknown heritage clause ${this}")
    }
}

fun translateHeritageNodes(parentEntities: List<HeritageNode>): String {
    val parents = if (parentEntities.isNotEmpty()) {
        " : " + parentEntities.map { parentEntity ->
            "${parentEntity.name.translate()}${translateTypeArguments(parentEntity.typeArguments)}"
        }.joinToString(", ")
    } else ""

    return parents
}

fun ParameterValueDeclaration.translateAsHeritageClause(): String {
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

fun DelegationModel.translate(): String {
    return when (this) {
        is ClassModel -> name
        is ExternalDelegationModel -> "definedExternally"
        else -> ""
    }
}

fun HeritageModel.translateAsHeritageClause(): String {
    val delegationClause = delegateTo?.let { " by ${it.translate()}" } ?: ""
    return "${value.translateAsHeritageClause()}${delegationClause}"
}


fun ClassModel.translate(nested: Boolean, padding: Int): String {
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

fun processDeclarations(docRoot: ModuleModel): List<String> {
    val res: MutableList<String> = mutableListOf()

    for (declaration in docRoot.declarations) {
        if (declaration is VariableModel) {
            res.add(declaration.translate())
        } else if (declaration is EnumNode) {
            res.add(declaration.translate())
        } else if (declaration is FunctionModel) {
            res.add(declaration.translate())
        } else if (declaration is ClassModel) {
            res.add(declaration.translate(false, 0))
        } else if (declaration is ObjectNode) {

            val objectNode = "external object ${declaration.name}"

            val members = declaration.members

            val hasMembers = members.isNotEmpty()

            res.add(objectNode + " {")

            if (hasMembers) {
                res.addAll(members.flatMap { it.translate() }.map({ "    " + it }))
            }

            res.add("}")
        } else if (declaration is InterfaceModel) {
            val hasMembers = declaration.members.isNotEmpty()
            val staticMembers = declaration.companionObject.members

            val showCompanionObject = staticMembers.isNotEmpty() || declaration.companionObject.parentEntities.isNotEmpty()

            val isBlock = hasMembers || staticMembers.isNotEmpty() || showCompanionObject
            val parents = translateHeritageNodes(declaration.parentEntities)

            res.add("${translateAnnotations(declaration.annotations)}external interface ${declaration.name}${translateTypeParameters(declaration.typeParameters)}${parents}" + if (isBlock) " {" else "")
            if (isBlock) {
                res.addAll(declaration.members.flatMap { it.translateSignature() }.map { "    " + it })

                val parents = if (declaration.companionObject.parentEntities.isEmpty()) {
                    ""
                } else {
                    " : ${declaration.companionObject.parentEntities.map { it.translateAsHeritageClause() }.joinToString(", ")}"
                }

                if (showCompanionObject) {
                    res.add("    companion object${parents} {")
                    res.addAll(staticMembers.flatMap { it.translate() }.map({ "        ${it}" }))
                    res.add("    }")
                }

                res.add("}")
            }


        }
    }

    return res
}


fun processModule(docRoot: ModuleModel): List<String> {
    val res: MutableList<String> = mutableListOf()
    if (docRoot.declarations.isEmpty() && docRoot.sumbodules.isEmpty()) {
        return res
    }

    val containsSomethingExceptDocRoot = docRoot.declarations.isNotEmpty()

    if (containsSomethingExceptDocRoot) {
        res.add("${translateAnnotations(docRoot.annotations)}package ${docRoot.packageName}")
        res.add("")
    }

    res.addAll(processDeclarations(docRoot))
    return res

}

fun translateModule(docRoot: ModuleModel): List<List<String>> {
    val list = listOf(processModule(docRoot)) + docRoot.sumbodules.map { submodule -> translateModule(submodule) }.flatten()
    return list.filter { it.isNotEmpty() }
}