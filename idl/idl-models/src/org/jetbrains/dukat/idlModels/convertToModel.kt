package org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.IndexStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementCallModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstantDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstructorDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlDeclarations.changeComment
import org.jetbrains.dukat.idlDeclarations.toNullable
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

fun IDLSingleTypeDeclaration.process(): TypeValueModel {
    val typeModel = TypeValueModel(
            value = IdentifierEntity(when (name) {
                "void" -> "Unit"
                "float" -> "Float"
                "unrestrictedfloat" -> "Float"
                "double" -> "Double"
                "unrestricteddouble" -> "Double"
                "long" -> "Int"
                "unsignedlong" -> "Int"
                "longlong" -> "Int"
                "unsignedlonglong" -> "Int"
                "octet" -> "Byte"
                "byte" -> "Byte"
                "short" -> "Short"
                "unsignedshort" -> "Short"
                "boolean" -> "Boolean"
                "ByteString" -> "String"
                "DOMString" -> "String"
                "USVString" -> "String"
                "\$Array" -> "Array"
                "sequence" -> "Array"
                "object" -> "dynamic"
                "DOMError" -> "dynamic"
                "\$dynamic" -> "dynamic"
                "any" -> "Any"
                else -> name
            }),
            params = listOfNotNull(typeParameter?.process()),
            metaDescription = comment
    )
    return typeModel.copy(
            nullable = when (typeModel.value) {
                IdentifierEntity("dynamic") -> false
                IdentifierEntity("Any") -> true
                else -> nullable
            }
    )
}

fun IDLFunctionTypeDeclaration.process(): FunctionTypeModel {
    return FunctionTypeModel(
            parameters = arguments.map { it.process() },
            type = returnType.process(),
            metaDescription = comment,
            nullable = nullable
    )
}

fun IDLTypeDeclaration.process(): TypeModel {
    return when (this) {
        is IDLSingleTypeDeclaration -> process()
        is IDLFunctionTypeDeclaration -> process()
        //there shouldn't be any UnionTypeDeclarations at this stage
        else -> raiseConcern("unprocessed type declaration: ${this}") { TypeValueModel(IdentifierEntity("IMPOSSIBLE"), listOf(), null) }
    }
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
    return if (callback || extendedAttributes.contains(IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject"))) {
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
            type = type.toNullable().changeComment(null).process(),
            initializer = if (defaultValue != null) {
                StatementCallModel(
                        IdentifierEntity(defaultValue!!),
                        null
                )
            } else {
                null
            },
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
        is IDLImplementsStatementDeclaration -> null
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
                type = type.toNullable().process(),
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