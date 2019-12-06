package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.astCommon.CommentEntity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.SimpleCommentEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.DelegationModel
import org.jetbrains.dukat.astModel.comments.DocumentationCommentModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.ExternalDelegationModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.ImportModel
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
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.Variance
import org.jetbrains.dukat.astModel.isGeneric
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.ChainCallModel
import org.jetbrains.dukat.astModel.statements.IndexStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementCallModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.stdlib.org.jetbrains.dukat.stdlib.TS_STDLIB_WHITE_LIST
import org.jetbrains.dukat.translator.LIB_PACKAGENAME
import org.jetbrains.dukat.translator.ModelVisitor
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME

const val FORMAT_TAB = "    "

private fun String?.translateMeta(): String {
    return if (this != null) {
        " /* ${this} */"
    } else {
        ""
    }
}


private fun CommentEntity.translate(): String {
    return when (this) {
        is SimpleCommentEntity -> text.translateMeta().trim()
        is DocumentationCommentModel -> {
"""/**
 * $text
 */"""
        }
        else -> ""
    }
}

private fun CommentEntity.translate(output: (String) -> Unit) {
    translate().split("\n").forEach(output)
}


private fun TypeModel.translateMeta(): String {
    return when (this) {
        is TypeParameterReferenceModel -> metaDescription.translateMeta()
        is TypeValueModel -> metaDescription.translateMeta()
        is FunctionTypeModel -> metaDescription.translateMeta()
        else -> ""
    }
}

private fun StatementModel.translateMeta(): String {
    return metaDescription.translateMeta()
}

fun TypeModel.translate(): String {
    return when (this) {
        is TypeParameterReferenceModel -> {
            val res = mutableListOf(name.translate())
            if (nullable) {
                res.add("?")
            }
            res.joinToString("")
        }
        is TypeValueModel -> {
            val res = mutableListOf(value.translate())
            if (isGeneric()) {
                res.add(translateTypeParameters(params))
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
            val varianceDescription = when (typeParameter.variance) {
                Variance.INVARIANT -> ""
                Variance.COVARIANT -> "out "
                Variance.CONTRAVARIANT -> "in "
            }
            val constraintDescription = if (typeParameter.constraints.isEmpty()) {
                ""
            } else {
                " : ${typeParameter.constraints[0].translate()}"
            }
            varianceDescription + typeParameter.type.translate() +
                    typeParameter.type.translateMeta() + constraintDescription
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
    val annotationsResolved = annotations.map { annotationNode ->
        var res = "@" + annotationNode.name
        if (annotationNode.params.isNotEmpty()) {
            res = res + "(" + annotationNode.params.joinToString(", ") { "\"${it.translate()}\"" } + ")"
        }
        res
    }

    val annotationTranslated = if (annotationsResolved.isEmpty()) "" else annotationsResolved.joinToString(LINE_SEPARATOR) + LINE_SEPARATOR

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
    comment?.translate(output)

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
            "${translateAnnotations(annotations)}${visibilityModifier.asClause()}${modifier}${operator} fun${typeParams} ${funName}(${translateParameters(parameters)})${returnClause}${type.translateMeta()}${bodyFirstLine}")

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
    val overrideClause = if (override != null) "override " else if (open) "open " else ""

    val metaClause = type.translateMeta()
    return annotations + listOf("${overrideClause}${operatorModifier}fun${typeParams} ${name.translate()}(${translateParameters(parameters)})${returnClause}$metaClause")
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
    return "${translateAnnotations(annotations)}${visibilityModifier.asClause()}${modifier} ${variableKeyword}${typeParams} ${varName}: ${type.translate()}${type.translateMeta()}${body}"
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
    val modifier = if (override != null) "override " else if (open) "open " else ""
    val varModifier = if (immutable) "val" else "var"

    return "$modifier$varModifier ${name.translate()}: ${type.translate()}${type.translateMeta()}"
}

private fun MemberModel.translate(): List<String> {
    return when (this) {
        is MethodModel -> translate()
        is PropertyModel -> listOf(translate())
        is ConstructorModel -> translate()
        is ClassModel -> listOf(translate(1))
        is InterfaceModel -> listOf(translate(1))
        else -> raiseConcern("can not translate MemberModel ${this}") { listOf("") }
    }
}

private fun ImportModel.translate(): String {
    return name.translate() + (asAlias?.let{ " as ${it}" } ?: "")
}

private fun PropertyModel.translateSignature(): List<String> {
    val varModifier = if (immutable) "val" else "var"
    val overrideClause = if (override != null) "override " else ""


    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }
    val metaClause = type.translateMeta()
    val res = mutableListOf(
            "${overrideClause}${varModifier}${typeParams} ${name.translate()}: ${type.translate()}${metaClause}"
    )
    if (getter) {
        res.add(FORMAT_TAB + "get() = definedExternally")
    }
    if (setter) {
        res.add(FORMAT_TAB + "set(value) = definedExternally")
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
    val overrideClause = if (override != null) "override " else ""

    val metaClause = type.translateMeta()
    val methodNodeTranslation = "${overrideClause}${operatorModifier}fun${typeParams} ${name.translate()}(${translateParameters(parameters)})${returnClause}$metaClause"
    return annotations + listOf(methodNodeTranslation)
}

private fun MemberModel.translateSignature(): List<String> {
    return when (this) {
        is MethodModel -> translateSignature()
        is PropertyModel -> translateSignature()
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
                "<${params.joinToString("::") { it.type.translateAsHeritageClause() }}>"
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

private fun ClassModel.translate(depth: Int): String {
    val res = mutableListOf<String>()
    translate(depth) { res.add(it) }
    return res.joinToString(LINE_SEPARATOR)
}

private fun VisibilityModifierModel.translate(): String? {
    return when (this) {
        VisibilityModifierModel.PUBLIC -> "public"
        VisibilityModifierModel.INTERNAL -> "internal"
        VisibilityModifierModel.PRIVATE -> "private"
        VisibilityModifierModel.PROTECTED -> "protected"
        VisibilityModifierModel.DEFAULT -> null
    }
}

private fun VisibilityModifierModel.asClause(): String {
    return translate()?.let { "$it " } ?: ""
}

private fun ClassModel.translate(depth: Int, output: (String) -> Unit) {
    val primaryConstructor = primaryConstructor
    val hasSecondaryConstructors = members.any { it is ConstructorModel }

    comment?.translate(output)

    val parents = translateHeritagModels(parentEntities)
    val externalClause = if (external) "${KOTLIN_EXTERNAL_KEYWORD} " else ""
    val params = if (primaryConstructor == null) "" else
        if (primaryConstructor.parameters.isEmpty() && !hasSecondaryConstructors) "" else "(${translateParameters(primaryConstructor.parameters)})"

    val openClause = if (abstract) "abstract" else "open"

    val classDeclaration = "${translateAnnotations(annotations)}${visibilityModifier.asClause()}${externalClause}${openClause} class ${name.translate()}${translateTypeParameters(typeParameters)}${params}${parents}"

    val members = members
    val staticMembers = companionObject?.members.orEmpty()

    val hasMembers = members.isNotEmpty()
    val hasStaticMembers = staticMembers.isNotEmpty()
    val isBlock = hasMembers || hasStaticMembers

    output(classDeclaration + if (isBlock) " {" else "")

    if (hasMembers) {
        members.flatMap { it.translate() }.map({ FORMAT_TAB.repeat(depth + 1) + it }).forEach {
            output(it)
        }
    }

    if (companionObject != null) {
        if (hasMembers) {
            output("")
        }
        output(FORMAT_TAB.repeat(depth + 1) + "companion object${if (!hasStaticMembers) "" else " {"}")
    }
    if (hasStaticMembers) {
        staticMembers.flatMap { it.translate() }.map({ FORMAT_TAB.repeat(depth + 2) + it }).forEach {
            output(it)
        }
        output(FORMAT_TAB.repeat(depth + 1) + "}")
    }

    if (isBlock) {
        output(FORMAT_TAB.repeat(depth) + "}")
    }
}

private fun InterfaceModel.translate(padding: Int): String {
    val res = mutableListOf<String>()
    translate(padding) { res.add(it) }
    return res.joinToString(LINE_SEPARATOR)
}

fun InterfaceModel.translate(padding: Int, output: (String) -> Unit) {

    comment?.translate(output)

    val hasMembers = members.isNotEmpty()
    val staticMembers = companionObject?.members.orEmpty()

    val isBlock = hasMembers || staticMembers.isNotEmpty() || companionObject != null
    val parents = translateHeritagModels(parentEntities)

    val externalClause = if (external) "${KOTLIN_EXTERNAL_KEYWORD} " else ""
    output("${translateAnnotations(annotations)}${visibilityModifier.asClause()}${externalClause}interface ${name.translate()}${translateTypeParameters(typeParameters)}${parents}" + if (isBlock) " {" else "")
    if (isBlock) {
        members.flatMap { it.translateSignature() }.map { FORMAT_TAB.repeat(padding + 1) + it }.forEach { output(it) }

        if (companionObject != null) {
            val parentsResolved = if (companionObject!!.parentEntities.isEmpty()) {
                ""
            } else {
                " : ${companionObject!!.parentEntities.map { it.translateAsHeritageClause() }.joinToString(", ")}"
            }

            if (hasMembers) {
                output("")
            }
            output("${FORMAT_TAB.repeat(padding + 1)}companion object${parentsResolved}${if (staticMembers.isEmpty()) "" else " {"}")

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
        addOutput("")
        addOutput(typeAlias.translate())
    }

    override fun visitVariable(variable: VariableModel) {
        addOutput("")
        addOutput(variable.translate())
    }

    override fun visitFunction(function: FunctionModel) {
        addOutput("")
        function.translate(0, ::addOutput)
    }

    override fun visitObject(objectNode: ObjectModel) {
        addOutput("")
        val objectModel = "${objectNode.visibilityModifier.asClause()}${KOTLIN_EXTERNAL_KEYWORD} object ${objectNode.name.translate()}"

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
        addOutput("")
        addOutput(enumNode.translate())
    }

    override fun visitInterface(interfaceModel: InterfaceModel) {
        addOutput("")
        interfaceModel.translate(0, ::addOutput)
    }

    override fun visitClass(classModel: ClassModel) {
        addOutput("")
        classModel.translate(0, ::addOutput)
    }

    fun visitImport(import: ImportModel) {
        val isLibImport = import.name.leftMost() == LIB_PACKAGENAME
        if (!isLibImport || TS_STDLIB_WHITE_LIST.contains(import.name.shiftLeft())) {
            addOutput("import ${import.translate()}")
        }
    }

    override fun visitModule(moduleModel: ModuleModel) {
        if (moduleModel.declarations.isEmpty() && moduleModel.submodules.isEmpty()) {
            return
        }

        val containsSomethingExceptDocRoot = moduleModel.declarations.isNotEmpty()

        if (containsSomethingExceptDocRoot) {
            val translateAnnotations = translateAnnotations(moduleModel.annotations)

            if ((moduleModel.name != ROOT_PACKAGENAME) && (moduleModel.name != LIB_PACKAGENAME)) {
                addOutput("${translateAnnotations}package ${moduleModel.name.translate()}")
                addOutput("")
            } else {
                if (translateAnnotations.isNotEmpty()) {
                    addOutput(translateAnnotations)
                }
            }
        }

        moduleModel.imports.forEachIndexed { _, importNode ->
            visitImport(importNode)
        }
    }

}