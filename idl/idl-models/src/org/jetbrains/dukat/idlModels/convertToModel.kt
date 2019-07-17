package org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.statements.*
import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

fun IDLTypeDeclaration.process(): TypeValueModel {
    return TypeValueModel(
            value = IdentifierEntity(when (name) {
                "void" -> "Unit"
                "float" -> "Float"
                "unrestrictedfloat" -> "Float"
                "double" -> "Double"
                "long" -> "Int"
                "unsignedlong" -> "Int"
                "byte" -> "Byte"
                "short" -> "Short"
                "longlong" -> "Long"
                "boolean" -> "Boolean"
                else -> name
            }),
            params = listOf(),
            metaDescription = null
    )
}

fun IDLArgumentDeclaration.process(): ParameterModel {
    return ParameterModel(
            name = name,
            type = type.process(),
            initializer = null,
            vararg = false,
            optional = false
    )
}

fun IDLInterfaceDeclaration.convertToModel(): TopLevelModel {
    return if (extendedAttributes.contains(IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject"))) {
        InterfaceModel(
                name = IdentifierEntity(name),
                members = attributes.filterNot { it.static }.mapNotNull { it.process() } +
                        operations.filterNot { it.static }.mapNotNull { it.process() },
                companionObject = CompanionObjectModel(
                        name = "",
                        members = constants.mapNotNull { it.process() } +
                                operations.filter { it.static }.mapNotNull { it.process() } +
                                attributes.filter { it.static }.mapNotNull { it.process() },
                        parentEntities = listOf()
                ),
                typeParameters = listOf(),
                parentEntities = parents.map {
                    HeritageModel(
                            it.process(),
                            listOf(),
                            null
                    )
                },
                annotations = mutableListOf(),
                external = true
        )
    } else {
        ClassModel(
                name = IdentifierEntity(name),
                members = attributes.filterNot { it.static }.mapNotNull { it.process() } +
                        operations.filterNot { it.static }.mapNotNull { it.process() } +
                        constructors.mapNotNull { it.process() },
                companionObject = CompanionObjectModel(
                        name = "",
                        members = constants.mapNotNull { it.process() } +
                                operations.filter { it.static }.mapNotNull { it.process() } +
                                attributes.filter { it.static }.mapNotNull { it.process() },
                        parentEntities = listOf()
                ),
                typeParameters = listOf(),
                parentEntities = parents.map {
                    HeritageModel(
                            it.process(),
                            listOf(),
                            null
                    )
                },
                primaryConstructor = if (primaryConstructor != null) {
                    primaryConstructor!!.process() as ConstructorModel
                } else {
                    null
                },
                annotations = mutableListOf(),
                external = true,
                abstract = constructors.isEmpty() && primaryConstructor == null
        )
    }
}

fun IDLDictionaryMemberDeclaration.convertToParameterModel(): ParameterModel {
    return ParameterModel(
            name = name,
            type = type.process().copy(nullable = true),
            initializer = null,
            vararg = false,
            optional = false
    )
}

fun IDLDictionaryMemberDeclaration.convertToAssignmentStatementModel(): AssignmentStatementModel {
    return AssignmentStatementModel(
            IndexStatementModel(
                    StatementCallModel(
                            IdentifierEntity("o"),
                            null
                    ),
                    StatementCallModel(
                            IdentifierEntity("\"$name\""),
                            null
                    )),
            StatementCallModel(
                    IdentifierEntity(name),
                    null
            )
    )
}

fun IDLDictionaryDeclaration.generateFunctionBody(): List<StatementModel> {
    val functionBody: MutableList<StatementModel> = mutableListOf(AssignmentStatementModel(
            StatementCallModel(
                    IdentifierEntity("val o"),
                    null
            ),
            StatementCallModel(
                    IdentifierEntity("js"),
                    listOf(IdentifierEntity("\"({})\""))
            )
    ))
    functionBody.addAll(members.map { it.convertToAssignmentStatementModel() })
    functionBody.add(ReturnStatementModel(StatementCallModel(
            IdentifierEntity("o"),
            null
    )))
    return functionBody
}

fun IDLDictionaryDeclaration.convertToModel(): List<TopLevelModel> {
    val declaration = InterfaceModel(
            name = IdentifierEntity(name),
            members = members.mapNotNull { it.process() },
            companionObject = CompanionObjectModel(
                    name = "",
                    members = listOf(),
                    parentEntities = listOf()
            ),
            typeParameters = listOf(),
            parentEntities = parents.map {
                HeritageModel(
                        it.process(),
                        listOf(),
                        null
                )
            },
            annotations = mutableListOf(),
            external = true
    )
    val generatedFunction = FunctionModel(
            name = IdentifierEntity(name),
            parameters = members.map { it.convertToParameterModel() },
            type = TypeValueModel(
                    value = IdentifierEntity(name),
                    params = listOf(),
                    metaDescription = null
            ),
            typeParameters = listOf(),
            annotations = mutableListOf(AnnotationModel(
                    name = "kotlin.internal.InlineOnly",
                    params = listOf()
            )),
            export = false,
            inline = true,
            operator = false,
            extend = null,
            body = generateFunctionBody()
    )
    return listOf(declaration, generatedFunction)
}

fun IDLTopLevelDeclaration.convertToModel(): List<TopLevelModel>? {
    return when (this) {
        is IDLInterfaceDeclaration -> listOf(convertToModel())
        is IDLDictionaryDeclaration -> convertToModel()
        is IDLTypedefDeclaration -> null
        else -> raiseConcern("unprocessed top level declaration: ${this}") { null }
    }
}

fun IDLMemberDeclaration.process(): MemberModel? {
    return when (this) {
        is IDLAttributeDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                getter = true,
                setter = !readOnly,
                open = false
        )
        is IDLOperationDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = arguments.map { it.process() },
                type = returnType.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                operator = false,
                annotations = listOf(),
                open = false
        )
        is IDLConstructorDeclaration -> ConstructorModel(
                parameters = arguments.map { it.process() },
                typeParameters = listOf(),
                generated = false
        )
        is IDLConstantDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                getter = true,
                setter = false,
                open = false
        )
        is IDLDictionaryMemberDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.process().copy(nullable = true),
                typeParameters = listOf(),
                static = false,
                override = false,
                getter = true,
                setter = true,
                open = false
        )
        else -> raiseConcern("unprocessed member declaration: ${this}") { null }
    }
}

fun IDLFileDeclaration.process(): SourceSetModel {
    val modelDeclarations = declarations.mapNotNull { it.convertToModel() }.flatten()

    val module = ModuleModel(
            name = ROOT_PACKAGENAME,
            shortName = ROOT_PACKAGENAME,
            declarations = modelDeclarations,
            annotations = mutableListOf(),
            submodules = listOf(),
            imports = mutableListOf()
    )

    val source = SourceFileModel(
            name = null,
            fileName = File(fileName).normalize().absolutePath,
            root = module,
            referencedFiles = listOf()
    )

    return SourceSetModel(listOf(source))
}