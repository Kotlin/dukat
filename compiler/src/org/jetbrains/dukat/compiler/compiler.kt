package org.jetbrains.dukat.compiler

import org.jetbrains.dukat.ast.model.isGeneric
import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.DelegationModel
import org.jetbrains.dukat.ast.model.model.ExternalDelegationModel
import org.jetbrains.dukat.ast.model.model.HeritageModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeNodeValue
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.compiler.translator.InputTranslator
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration

private fun ParameterValueDeclaration.translateMeta(): String {

    val skipNullableAnnotation = (this is TypeNode) && (value is IdentifierNode) && ((value as IdentifierNode).value == "Nothing")
    if (nullable && !skipNullableAnnotation) {
        //TODO: consider rethinking this restriction
        return " /*= null*/"
    }

    return translateSignatureMeta()
}

private fun ParameterValueDeclaration.translateSignatureMeta(): String {
    val meta = this.meta
    return when (meta) {
        is StringTypeDeclaration -> " /* ${meta.tokens.joinToString("|")} */"
        is ThisTypeInGeneratedInterfaceMetaData -> " /* this */"
        is IntersectionMetadata -> " /* ${meta.params.map { it.translate() }.joinToString(" & ")} */"
        else -> ""
    }

}

private fun IdentifierNode.translate(): String {
    return value
}

private fun QualifiedLeftNode.translate() : String {
    return when(this) {
        is QualifiedNode -> translate()
        is IdentifierNode -> translate()
        else -> throw Exception("unknown ")
    }
}

private fun QualifiedNode.translate(): String {
    val translateLeft = left.translate()
    val translaterRight = right.translate()

    return translateLeft + "." + translaterRight
}


private fun TypeNodeValue.translate(): String {
    return when(this) {
        is IdentifierNode -> translate()
        else -> throw Exception("unknown TypeNodeValue ${this}")
    }
}


private fun ParameterValueDeclaration.translate(): String {
    if (this is TypeNode) {
        val res = mutableListOf(value.translate())
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
    } else if (this is FunctionTypeNode) {
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
    } else if (this is UnionTypeNode) {
        return translate()
    } else if (this is GeneratedInterfaceReferenceDeclaration) {
        return "${name}${translateTypeParameters(typeParameters)}"
    } else if (this is QualifiedNode) {
        return translate()
    } else if (this is TupleDeclaration) {
        return translate()
    } else if (this is IdentifierDeclaration) {
        return value
    } else {
        return "failed to translateType ${this}"
    }
}

private fun UnionTypeNode.translate(): String {
    val metaBody = params.map { it.translate() }.joinToString(" | ")
    val meta = "/* ${metaBody} */"
    return "dynamic ${meta}"
}

private fun TupleDeclaration.translate(): String {
    val metaBody = params.map { it.translate() }.joinToString(", ")
    val meta = "/* JsTuple<${metaBody}> */"
    return "dynamic ${meta}"
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

private fun translateTypeArguments(typeParameters: List<IdentifierNode>): String {
    if (typeParameters.isEmpty()) {
        return ""
    } else {
        return "<" + typeParameters.map { it.value }.joinToString(", ") + ">"
    }
}


private fun translateParameters(parameters: List<ParameterDeclaration>, needsMeta: Boolean = true): String {
    return parameters
            .map { parameter -> parameter.translate(needsMeta) }
            .joinToString(", ")
}

private fun translateAnnotations(annotations: List<AnnotationNode>): String {
    val annotations = annotations.map { annotationNode ->
        var res = "@" + annotationNode.name
        if (annotationNode.params.isNotEmpty()) {
            res = res + "(" + annotationNode.params.joinToString(", ") { "\"${it}\"" } + ")"
        }
        res
    }

    val annotationTranslated = if (annotations.isEmpty()) "" else annotations.joinToString("\n") + "\n"

    return annotationTranslated
}

private fun FunctionNode.translate(): String {
    val returnType = type.translate()

    var typeParams = translateTypeParameters(typeParameters)
    if (typeParams.isNotEmpty()) {
        typeParams = " " + typeParams
    }

    return ("${translateAnnotations(annotations)}external fun${typeParams} ${name}(${translateParameters(parameters)}): ${returnType} = definedExternally")
}

private fun MethodNode.translate(): List<String> {
    val returnsUnit = type == TypeNode("@@None", emptyList())
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

    return annotations + listOf("${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters, !override)})${returnClause}${type.translateSignatureMeta()}${definedExternallyClause}")
    return annotations + listOf("${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters, !override)})${returnClause}${type.translateSignatureMeta()}${definedExternallyClause}")
}

private fun ConstructorNode.translate(): List<String> {
    var typeParams = translateTypeParameters(typeParameters)
    return listOf("constructor${typeParams}(${translateParameters(parameters, false)})")
}

private fun VariableNode.translate(): String {
    val variableKeyword = if (immutable) "val" else "var"
    return "${translateAnnotations(annotations)}external ${variableKeyword} ${name}: ${type.translate()}${type.translateSignatureMeta()} = definedExternally"
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

private fun PropertyNode.translate(): String {
    val open = !static && open
    val modifier = if (override) "override " else if (open) "open " else ""

    val definedExternallyClause = if (definedExternally) " = definedExternally" else ""
    return "${modifier}var ${name}: ${type.translate()}${type.translateSignatureMeta()}${definedExternallyClause}"
}

private fun MemberNode.translate(): List<String> {
    if (this is MethodNode) {
        return translate()
    } else if (this is PropertyNode) {
        return listOf(translate())
    } else if (this is ConstructorNode) {
        return translate()
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

    val returnsUnit = type == TypeNode("Unit", emptyList())
    val returnClause = if (returnsUnit) "" else ": ${type.translate()}${type.translateSignatureMeta()}"
    val overrideClause = if (override) "override " else ""

    val methodNodeTranslation = "${overrideClause}${operatorModifier}fun${typeParams} ${name}(${translateParameters(parameters)})${returnClause}"
    return annotations + listOf(methodNodeTranslation)
}

private fun MemberNode.translateSignature(): List<String> {
    if (this is MethodNode) {
        return translateSignature()
    } else if (this is PropertyNode) {
        return listOf(translateSignature())
    } else {
        throw Exception("can not translate singature ${this}")
    }
}


private fun IdentifierDeclaration.translate() = value

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

private fun ParameterValueDeclaration.translateAsHeritageClause(): String {
    return when (this) {
        is FunctionTypeNode -> translate()
        is TypeNode -> {
            val typeParams = if (params.isEmpty()) {
                ""
            } else {
                "<${params.joinToString("::") { it.translateAsHeritageClause() }}>"
            }

            when (value) {
                is IdentifierNode -> "${(value as IdentifierNode).value}${typeParams}"
                else -> throw Exception("unknown TypeNodeValue ${value}")
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


private fun processDeclarations(docRoot: ModuleModel): List<String> {
    val res: MutableList<String> = mutableListOf()

    for (declaration in docRoot.declarations) {
        if (declaration is VariableNode) {
            res.add(declaration.translate())
        } else if (declaration is EnumNode) {
            res.add(declaration.translate())
        } else if (declaration is FunctionNode) {
            res.add(declaration.translate())
        } else if (declaration is ClassModel) {
            val primaryConstructor = declaration.primaryConstructor

            val parents = translateHeritageNodes(declaration.parentEntities)
            val classDeclaration = "${translateAnnotations(declaration.annotations)}external open class ${declaration.name}${translateTypeParameters(declaration.typeParameters)}${parents}"
            val params = if (primaryConstructor == null) "" else
                if (primaryConstructor.parameters.isEmpty()) "" else "(${translateParameters(primaryConstructor.parameters)})"

            val members = declaration.members
            val staticMembers = declaration.companionObject.members

            val hasMembers = members.isNotEmpty()
            val hasStaticMembers = staticMembers.isNotEmpty()
            val isBlock = hasMembers || hasStaticMembers

            res.add(classDeclaration + params + if (isBlock) " {" else "")

            if (hasMembers) {
                res.addAll(members.flatMap { it.translate() }.map({ "    $it" }))
            }

            if (staticMembers.isNotEmpty()) {
                res.add("    companion object {")
                res.addAll(staticMembers.flatMap { it.translate() }.map({ "        ${it}" }))
                res.add("    }")
            }


            if (isBlock) {
                res.add("}")
            }

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


private fun processModule(docRoot: ModuleModel): List<String> {
    val res: MutableList<String> = mutableListOf<String>()
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

private fun translateModule(docRoot: ModuleModel): List<List<String>> {
    val list = listOf(processModule(docRoot)) + docRoot.sumbodules.map { submodule -> translateModule(submodule) }.flatten()
    return list.filter { it.isNotEmpty() }
}

fun compile(documentRoot: ModuleModel): String {
    var res = translateModule(documentRoot).joinToString("""

// ------------------------------------------------------------------------------------------
""") { it.joinToString("\n") }

    if (res == "") {
        res = "// NO DECLARATIONS"
    }
    return res
}

fun output(fileName: String, translator: InputTranslator): String {
    val sourceSetDeclaration = translator.translateFile(fileName)
    val sourcesMap = mutableMapOf<String, SourceFileDeclaration>()
    sourceSetDeclaration.sources.map { sourceFileDeclaration ->
       sourcesMap[sourceFileDeclaration.fileName] = sourceFileDeclaration
    }

    val documentRoot =
            translator.lower(sourcesMap.get(fileName)!!)
    return compile(documentRoot)
}