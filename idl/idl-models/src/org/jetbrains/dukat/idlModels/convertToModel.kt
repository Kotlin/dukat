package org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.statements.*
import org.jetbrains.dukat.idlDeclarations.*
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

fun IDLSetterDeclaration.process(ownerName: NameEntity): FunctionModel {
    return FunctionModel(
            name = IdentifierEntity(name),
            parameters = listOf(key.process(), value.process()),
            type = TypeValueModel(
                    value = IdentifierEntity("Unit"),
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
            operator = true,
            extend = ClassLikeReferenceModel(
                    name = ownerName,
                    typeParameters = listOf()
            ),
            body = listOf(AssignmentStatementModel(
                    IndexStatementModel(
                            StatementCallModel(
                                    IdentifierEntity("asDynamic"),
                                    listOf()
                            ),
                            StatementCallModel(
                                    IdentifierEntity(key.name),
                                    null
                            )
                    ),
                    StatementCallModel(
                            IdentifierEntity(value.name),
                            null
                    )
            ))
    )
}

fun IDLGetterDeclaration.process(ownerName: NameEntity): FunctionModel {
    return FunctionModel(
            name = IdentifierEntity(name),
            parameters = listOf(key.process()),
            type = valueType.process(),
            typeParameters = listOf(),
            annotations = mutableListOf(AnnotationModel(
                    name = "kotlin.internal.InlineOnly",
                    params = listOf()
            )),
            export = false,
            inline = true,
            operator = true,
            extend = ClassLikeReferenceModel(
                    name = ownerName,
                    typeParameters = listOf()
            ),
            body = listOf(ReturnStatementModel(
                    IndexStatementModel(
                            StatementCallModel(
                                    IdentifierEntity("asDynamic"),
                                    listOf()
                            ),
                            StatementCallModel(
                                    IdentifierEntity(key.name),
                                    null
                            )
                    )
            ))
    )
}

fun IDLInterfaceDeclaration.convertToModel(): List<TopLevelModel> {
    val dynamicMemberModels = (constructors +
            attributes.filterNot { it.static } +
            operations.filterNot { it.static }).mapNotNull { it.process() }
    val staticMemberModels = (constants +
            operations.filter { it.static } +
            attributes.filter { it.static }).mapNotNull { it.process() }

    val companionObjectModel = if (staticMemberModels.isNotEmpty()) {
        CompanionObjectModel(
                name = "",
                members = staticMemberModels,
                parentEntities = listOf()
        )
    } else {
        null
    }

    val parentModels = parents.map {
        HeritageModel(
                it.process(),
                listOf(),
                null
        )
    }

    val declaration = if (
            callback || extendedAttributes.contains(
                    IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject")
            )
    ) {
        InterfaceModel(
                name = IdentifierEntity(name),
                members = dynamicMemberModels,
                companionObject = companionObjectModel,
                typeParameters = listOf(),
                parentEntities = parentModels,
                annotations = mutableListOf(),
                external = true
        )
    } else {
        ClassModel(
                name = IdentifierEntity(name),
                members = dynamicMemberModels.map {
                    if (it is PropertyModel && !it.setter) {
                        it.copy(open = true)
                    } else {
                        it
                    }
                },
                companionObject = companionObjectModel,
                typeParameters = listOf(),
                parentEntities = parentModels,
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
    val getterModels = getters.map { it.process(declaration.name) }
    val setterModels = setters.map { it.process(declaration.name) }
    return listOf(declaration) + getterModels + setterModels
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
            companionObject = null,
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

fun processEnumMember(memberName: String): String {
    return memberName.toUpperCase().replace('-', '_').ifEmpty { "EMPTY" }
}

fun IDLEnumDeclaration.convertToModel(): List<TopLevelModel> {
    val declaration = InterfaceModel(
            name = IdentifierEntity(name),
            members = listOf(),
            companionObject = CompanionObjectModel(
                    name = "",
                    members = listOf(),
                    parentEntities = listOf()
            ),
            typeParameters = listOf(),
            parentEntities = listOf(),
            annotations = mutableListOf(),
            external = true
    )
    val generatedVariables = members.map { memberName ->
        VariableModel(
                name = IdentifierEntity(processEnumMember(memberName)),
                type = TypeValueModel(
                        value = declaration.name,
                        params = listOf(),
                        metaDescription = null
                ),
                annotations = mutableListOf(),
                immutable = true,
                inline = true,
                initializer = null,
                get = ChainCallModel(
                        StatementCallModel(
                                value = QualifierEntity(
                                        left = IdentifierEntity("\"$memberName\""),
                                        right = IdentifierEntity("asDynamic")
                                ),
                                params = listOf()
                        ),
                        StatementCallModel(
                                value = IdentifierEntity("unsafeCast"),
                                params = listOf(),
                                typeParameters = listOf(IdentifierEntity(name))
                        )
                ),
                set = null,
                typeParameters = listOf(),
                extend = ClassLikeReferenceModel(
                        name = QualifierEntity(
                                IdentifierEntity(name),
                                IdentifierEntity("Companion")
                        ),
                        typeParameters = listOf()
                )
        )
    }
    return listOf(declaration) + generatedVariables
}

fun IDLTopLevelDeclaration.convertToModel(): List<TopLevelModel>? {
    return when (this) {
        is IDLInterfaceDeclaration -> convertToModel()
        is IDLDictionaryDeclaration -> convertToModel()
        is IDLEnumDeclaration -> convertToModel()
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