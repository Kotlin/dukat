package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.DelegationModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.isGeneric
import org.jetbrains.dukat.astModel.statements.*
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ModelVisitor
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import translate

const val FORMAT_TAB = "    "

private fun String?.translateMeta(): String {
    return if (this != null) {
        " /* ${this} */"
    } else {
        ""
    }
}

private fun TypeModel.translateMeta(): String {
    return when (this) {
        is TypeValueModel -> metaDescription.translateMeta()
        is FunctionTypeModel -> metaDescription.translateMeta()
        else -> ""
    }
}

private fun StatementModel.translateMeta(): String {
    return metaDescription.translateMeta()
}

private fun translateTypeParams(params: List<TypeModel>): String {
    return "<" + params.joinToString(", ") { param -> "${param.translate()}${param.translateMeta()}" } + ">"
}

fun TypeModel.translate(): String {
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
                val paramNameSerialized = if (param.name != "") {
                    param.name + ": "
                } else {
                    ""
                }
                val paramSerialized = paramNameSerialized + param.type.translate() + param.type.translateMeta()
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
        else -> return "failed to translateType ${this}"
    }
}

private fun ParameterModel.translate(needsMeta: Boolean = true): String {
    var res = name + ": " + type.translate()
    if (vararg) {
        res = "vararg $res"
    }

    if (needsMeta) {
        res += type.translateMeta()
    }

    if (initializer != null) {
        res += " = ${initializer!!.translate()}"
        if (needsMeta) {
            res += initializer!!.translateMeta()
        }
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
            typeParameter.name.translate() + constraintDescription
        }.joinToString(", ") + ">"
    }
}

private fun translateTypeArguments(typeParameters: List<TypeModel>): String {
    return if (typeParameters.isEmpty()) {
        ""
    } else {
        "<" + typeParameters.map {
            it.translate() + it.translateMeta()
        }.joinToString(", ") + ">"
    }
}


private fun translateParameters(parameters: List<ParameterModel>, needsMeta: Boolean = true): String {
    return parameters
            .map { parameter -> parameter.translate(needsMeta) }
            .joinToString(", ")
}

private fun translateAnnotations(annotations: List<AnnotationModel>): String {
    val annotations = annotations.map { annotationNode ->
        var res = "@" + annotationNode.name
        if (annotationNode.params.isNotEmpty()) {
            res = res + "(" + annotationNode.params.joinToString(", ") { "\"${it.translate()}\"" } + ")"
        }
        res
    }

    val annotationTranslated = if (annotations.isEmpty()) "" else annotations.joinToString(LINE_SEPARATOR) + LINE_SEPARATOR

    return annotationTranslated
}

private fun StatementCallModel.translate(): String {
    return "${value.translate()}${if (typeParameters.isEmpty()) "" else "<${typeParameters.joinToString(", ") { it.value }}>"}${if (params == null) "" else "(${params?.joinToString(", ") { it.value }})"}"
}

private fun StatementModel.translate(): String {
    return when (this) {
        is AssignmentStatementModel -> "${left.translate()} = ${right.translate()}"
        is ChainCallModel -> "${left.translate()}.${right.translate()}"
        is ReturnStatementModel -> "return ${statement.translate()}"
        is IndexStatementModel -> "${array.translate()}[${index.translate()}]"
        is StatementCallModel -> translate()
        else -> raiseConcern("unkown StatementNode ${this}") { "" }
    }
}

private fun ClassLikeReferenceModel.translate(): String {
    return name.translate() + if (typeParameters.isNotEmpty()) {
        "<${typeParameters.map { it.translate() }.joinToString(", ")}>"
    } else {
        ""
    }
}

private fun FunctionModel.translate(padding: Int, output: (String) -> Unit) {
    val returnsUnit = (type is TypeValueModel) &&
            (type as TypeValueModel).value == IdentifierEntity("Unit")

    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val modifier = if (inline) "inline" else KOTLIN_EXTERNAL_KEYWORD
    val operator = if (operator) " operator" else ""

    val bodyFirstLine = if (body.isEmpty()) {
        ""
    } else if (body.size == 1) {
        if (body[0] is ReturnStatementModel) {
            " = ${(body[0] as ReturnStatementModel).statement.translate()}"
        } else {
            " { ${body[0].translate()} }"
        }
    } else {
        " {"
    }

    val funName = if (extend == null) {
        name.translate()
    } else {
        extend?.translate() + "." + name.translate()
    }

    output(FORMAT_TAB.repeat(padding) +
            "${translateAnnotations(annotations)}${modifier}${operator} fun${typeParams} ${funName}(${translateParameters(parameters)})${returnClause}${type.translateMeta()}${bodyFirstLine}")

    if (body.size > 1) {
        body.forEach { statement ->
            output(FORMAT_TAB.repeat(padding + 1) + statement.translate())
        }
        output(FORMAT_TAB.repeat(padding) + "}")
    }
}

private fun MethodModel.translate(): List<String> {
    val returnsUnit = (type is TypeValueModel) &&
            ((type as TypeValueModel).value == IdentifierEntity("@@None")
                    || (type as TypeValueModel).value == IdentifierEntity("Unit"))
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    val operatorModifier = if (operator) "operator " else ""
    val annotations = annotations.map { "@${it.name}" }

    val open = !static && open
    val overrideClause = if (override) "override " else if (open) "open " else ""

    val metaClause = type.translateMeta()
    return annotations + listOf("${overrideClause}${operatorModifier}fun${typeParams} ${name.translate()}(${translateParameters(parameters, !override)})${returnClause}$metaClause")
}

private fun ConstructorModel.translate(): List<String> {
    val typeParams = translateTypeParameters(typeParameters)
    return listOf("constructor${typeParams}(${translateParameters(parameters, false)})")
}

private fun TypeAliasModel.translate(): String {
    return "typealias ${name.translate()}${translateTypeParameters(typeParameters)} = ${typeReference.translate()}"
}

private fun VariableModel.translate(): String {
    val variableKeyword = if (immutable) "val" else "var"
    val modifier = if (inline) "inline" else KOTLIN_EXTERNAL_KEYWORD

    val body = if (initializer != null) {
        " = ${initializer?.translate()}"
    } else if ((get != null) && (set != null)) {
        val getter = "get() = ${get?.translate()};"
        val setter = "set(value) { ${set?.translate()} }"
        " ${getter} ${setter}"
    } else if (get != null) {
        val getter = "get() = ${get?.translate()}"
        " ${getter}"
    } else ""

    val typeParams = if (typeParameters.isEmpty()) {
        ""
    } else {
        " ${translateTypeParameters(typeParameters)}"
    }

    val varName = if (extend == null) {
        name.translate()
    } else {
        extend?.translate() + "." + name.translate()
    }
    return "${translateAnnotations(annotations)}${modifier} ${variableKeyword}${typeParams} ${varName}: ${type.translate()}${type.translateMeta()}${body}"
}

private fun EnumModel.translate(): String {
    val res = mutableListOf("${KOTLIN_EXTERNAL_KEYWORD} enum class ${name.translate()} {")
    res.add(values.map { value ->
        val metaClause = if (value.meta.isEmpty()) "" else " /* = ${value.meta} */"
        "    ${value.value}${metaClause}"
    }.joinToString(",${LINE_SEPARATOR}"))
    res.add("}")
    return res.joinToString(LINE_SEPARATOR)
}

private fun PropertyModel.translate(): String {
    val open = !static && open
    val modifier = if (override) "override " else if (open) "open " else ""
    val varModifier = if (getter && !setter) "val" else "var"

    return "$modifier$varModifier ${name.translate()}: ${type.translate()}${type.translateMeta()}"
}

private fun MemberModel.translate(): List<String> {
    return when (this) {
        is MethodModel -> translate()
        is PropertyModel -> listOf(translate())
        is ConstructorModel -> translate()
        is ClassModel -> listOf(translate(1))
        is InterfaceModel -> listOf(translate(1))
        else -> raiseConcern("can not translate MemberModel ${this::class.simpleName}") { listOf("") }
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
    var res = "${overrideClause}${varModifier}${typeParams} ${name.translate()}: ${type.translate()}${metaClause}"
    if (type.nullable) {
        if (getter) {
            res += " get() = definedExternally"
        }
        if (setter) {
            res += "; set(value) = definedExternally"
        }
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

    val returnsUnit = (type is TypeValueModel) && ((type as TypeValueModel).value == IdentifierEntity("Unit"))
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}"
    val overrideClause = if (override) "override " else ""

    val metaClause = type.translateMeta()
    val methodNodeTranslation = "${overrideClause}${operatorModifier}fun${typeParams} ${name.translate()}(${translateParameters(parameters)})${returnClause}$metaClause"
    return annotations + listOf(methodNodeTranslation)
}

private fun MemberModel.translateSignature(): List<String> {
    return when (this) {
        is MethodModel -> translateSignature()
        is PropertyModel -> listOf(translateSignature())
        is ClassModel -> listOf(translate(1))
        is InterfaceModel -> listOf(translate(1))
        else -> raiseConcern("can not translate signature ${this}") { emptyList<String>() }
    }
}

private fun translateHeritagModels(parentEntities: List<HeritageModel>): String {
    val parents = if (parentEntities.isNotEmpty()) {
        " : " + parentEntities.map { parentEntity ->
            "${parentEntity.value.translate()}${translateTypeArguments(parentEntity.typeParams)}"
        }.joinToString(", ")
    } else ""

    return parents
}

private fun TypeModel.translateAsHeritageClause(): String {
    return when (this) {
        is FunctionTypeModel -> translate()
        is TypeValueModel -> {
            val typeParams = if (params.isEmpty()) {
                ""
            } else {
                "<${params.joinToString("::") { it.translateAsHeritageClause() }}>"
            }

            when (value) {
                is IdentifierEntity -> "${(value as IdentifierEntity).value}${typeParams}"
                else -> raiseConcern("unknown NameEntity ${value}") { "" }
            }
        }
        else -> ""
    }
}

private fun DelegationModel.translate(): String {
    return when (this) {
        is ClassModel -> name.translate()
        is ExternalDelegationModel -> "definedExternally"
        else -> ""
    }
}

private fun HeritageModel.translateAsHeritageClause(): String {
    val delegationClause = delegateTo?.let { " by ${it.translate()}" } ?: ""
    return "${value.translateAsHeritageClause()}${delegationClause}"
}

private fun ClassModel.translate(padding: Int): String {
    val res = mutableListOf<String>()
    translate(padding) { res.add(it) }
    return res.joinToString(LINE_SEPARATOR)
}

private fun ClassModel.translate(padding: Int, output: (String) -> Unit) {
    val primaryConstructor = primaryConstructor

    val parents = translateHeritagModels(parentEntities)
    val externalClause = if (external) "${KOTLIN_EXTERNAL_KEYWORD} " else ""
    val params = if (primaryConstructor == null) "" else
        if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

    val openClause = if (abstract) "abstract" else "open"
    val classDeclaration = "${translateAnnotations(annotations)}${externalClause}${openClause} class ${name.translate()}${translateTypeParameters(typeParameters)}${params}${parents}"

    val members = members
    val staticMembers = companionObject?.members.orEmpty()

    val hasMembers = members.isNotEmpty()
    val hasStaticMembers = staticMembers.isNotEmpty()
    val isBlock = hasMembers || hasStaticMembers

    output(classDeclaration + if (isBlock) " {" else "")

    if (hasMembers) {
        members.flatMap { it.translate() }.map({ FORMAT_TAB.repeat(padding + 1) + it }).forEach {
            output(it)
        }
    }

    if (companionObject != null) {
        output(FORMAT_TAB.repeat(padding + 1) + "companion object${if (!hasStaticMembers) "" else " {"}")
    }
    if (hasStaticMembers) {
        staticMembers.flatMap { it.translate() }.map({ FORMAT_TAB.repeat(padding + 2) + it }).forEach {
            output(it)
        }
        output(FORMAT_TAB.repeat(padding + 1) + "}")
    }

    if (isBlock) {
        output(FORMAT_TAB.repeat(padding) + "}")
    }
}

private fun InterfaceModel.translate(padding: Int): String {
    val res = mutableListOf<String>()
    translate(padding) { res.add(it) }
    return res.joinToString(LINE_SEPARATOR)
}

fun InterfaceModel.translate(padding: Int, output: (String) -> Unit) {
    val hasMembers = members.isNotEmpty()
    val staticMembers = companionObject?.members.orEmpty()

    val isBlock = hasMembers || staticMembers.isNotEmpty() || companionObject != null
    val parents = translateHeritagModels(parentEntities)

    if (metaDescription != null) {
        output(metaDescription.translateMeta().trim())
    }

    val externalClause = if (external) "${KOTLIN_EXTERNAL_KEYWORD} " else ""
    output("${translateAnnotations(annotations)}${externalClause}interface ${name.translate()}${translateTypeParameters(typeParameters)}${parents}" + if (isBlock) " {" else "")
    if (isBlock) {
        members.flatMap { it.translateSignature() }.map { FORMAT_TAB.repeat(padding + 1) + it }.forEach { output(it) }

        if (companionObject != null) {
            val parents = if (companionObject!!.parentEntities.isEmpty()) {
                ""
            } else {
                " : ${companionObject!!.parentEntities.map { it.translateAsHeritageClause() }.joinToString(", ")}"
            }

            output("${FORMAT_TAB.repeat(padding + 1)}companion object${parents}${if (staticMembers.isEmpty()) "" else " {"}")

            if (staticMembers.isNotEmpty()) {
                staticMembers.flatMap { it.translate() }.map { "${FORMAT_TAB.repeat(padding + 2)}${it}" }.forEach { output(it) }
                output("${FORMAT_TAB.repeat(padding + 1)}}")
            }
        }

        output("${FORMAT_TAB.repeat(padding)}}")
    }
}


class StringTranslator : ModelVisitor {
    private var myOutput: MutableList<String> = mutableListOf()

    private fun addOutput(fragment: String) {
        myOutput.add(fragment)
    }

    fun output(): String {
        return myOutput.joinToString(LINE_SEPARATOR)
    }

    override fun visitTypeAlias(typeAlias: TypeAliasModel) {
        addOutput(typeAlias.translate())
    }

    override fun visitVariable(variable: VariableModel) {
        addOutput(variable.translate())
    }

    override fun visitFunction(function: FunctionModel) {
        function.translate(0, ::addOutput)
    }

    override fun visitObject(objectNode: ObjectModel) {
        val objectModel = "${KOTLIN_EXTERNAL_KEYWORD} object ${objectNode.name.translate()}"

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

    override fun visitEnum(enumNode: EnumModel) {
        addOutput(enumNode.translate())
    }

    override fun visitInterface(interfaceModel: InterfaceModel) {
        interfaceModel.translate(0, ::addOutput)
    }

    override fun visitClass(classModel: ClassModel) {
        classModel.translate(0, ::addOutput)
    }

    fun visitImport(import: NameEntity) {
        addOutput("import ${import.translate()}")
    }

    override fun visitModule(moduleModel: ModuleModel) {
        if (moduleModel.declarations.isEmpty() && moduleModel.submodules.isEmpty()) {
            return
        }

        val containsSomethingExceptDocRoot = moduleModel.declarations.isNotEmpty()

        if (containsSomethingExceptDocRoot) {
            val translateAnnotations = translateAnnotations(moduleModel.annotations)

            if (moduleModel.name != ROOT_PACKAGENAME) {
                addOutput("${translateAnnotations}package ${moduleModel.name.translate()}")
                addOutput("")
            } else {
                addOutput(translateAnnotations)
            }
        }

        moduleModel.imports.forEachIndexed { index, importNode ->
            visitImport(importNode)
            if (index == (moduleModel.imports.size - 1)) {
                addOutput("")
            }
        }
    }

}