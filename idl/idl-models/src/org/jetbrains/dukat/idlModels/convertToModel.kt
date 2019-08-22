package org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
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
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SimpleCommentModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.Variance
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.ChainCallModel
import org.jetbrains.dukat.astModel.statements.IndexStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementCallModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstructorDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLEnumDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFunctionTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLGetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLIncludesStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLNamespaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSimpleExtendedAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceKind
import org.jetbrains.dukat.idlDeclarations.changeComment
import org.jetbrains.dukat.idlDeclarations.processEnumMember
import org.jetbrains.dukat.idlDeclarations.toNullable
import org.jetbrains.dukat.idlDeclarations.toNullableIfNotPrimitive
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
                "unsignedlonglong" -> "Number"
                "octet" -> "Byte"
                "byte" -> "Byte"
                "short" -> "Short"
                "unsignedshort" -> "Short"
                "boolean" -> "Boolean"
                "ByteString" -> "String"
                "DOMString" -> "String"
                "String" -> "String"
                "USVString" -> "String"
                "\$Array" -> "Array"
                "sequence" -> "Array"
                "FrozenArray" -> "Array"
                "Promise" -> "Promise"
                "object" -> "dynamic"
                "DOMError" -> "dynamic"
                "\$dynamic" -> "dynamic"
                "any" -> "Any"
                else -> name
            }),
            params = listOfNotNull(typeParameter?.process())
                    .map { TypeParameterModel(it, listOf()) }
                    .map {
                        if (name == "FrozenArray") {
                            it.copy(variance = Variance.COVARIANT)
                        } else {
                            it
                        }
                    },
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

    val returnTypeModel = if (returnType.name == "any") {
        TypeValueModel(IdentifierEntity("dynamic"), listOf(), null)
    } else {
        returnType.process()
    }

    return FunctionTypeModel(
            parameters = arguments.filterNot { it.variadic }.map { it.process() },
            type = returnTypeModel,
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
            initializer = if (optional || defaultValue != null) {
                StatementCallModel(
                        IdentifierEntity("definedExternally"),
                        null
                )
            } else {
                null
            },
            vararg = variadic,
            optional = false
    )
}

fun IDLSetterDeclaration.processAsTopLevel(ownerName: NameEntity): FunctionModel {
    return FunctionModel(
            name = IdentifierEntity("set"),
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

fun IDLGetterDeclaration.processAsTopLevel(ownerName: NameEntity): FunctionModel {
    return FunctionModel(
            name = IdentifierEntity("get"),
            parameters = listOf(key.process()),
            type = valueType.toNullableIfNotPrimitive().process(),
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
    if (mixin) {
        return listOf()
    }

    val dynamicMemberModels = (constructors +
            attributes.filterNot { it.static } +
            operations.filterNot { it.static } +
            getters.filterNot { it.name == "get" } +
            setters.filterNot { it.name == "set" }).mapNotNull { it.process() }.distinct()
    val staticMemberModels = (attributes.filter { it.static } +
            operations.filter { it.static }).mapNotNull { it.process() }.distinct()

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

    val annotationModels = listOfNotNull(if (companionObjectModel != null) {
        AnnotationModel(
                "Suppress",
                listOf(IdentifierEntity("NESTED_CLASS_IN_EXTERNAL_INTERFACE"))
        )
    } else {
        null
    }).toMutableList()

    val declaration = if (
            kind == InterfaceKind.INTERFACE) {
        InterfaceModel(
                name = IdentifierEntity(name),
                members = dynamicMemberModels,
                companionObject = companionObjectModel,
                typeParameters = listOf(),
                parentEntities = parentModels,
                comment = null,
                annotations = annotationModels,
                external = true
        )
    } else {
        ClassModel(
                name = IdentifierEntity(name),
                members = dynamicMemberModels,
                companionObject = companionObjectModel,
                typeParameters = listOf(),
                parentEntities = parentModels,
                primaryConstructor = if (primaryConstructor != null) {
                    primaryConstructor!!.process() as ConstructorModel
                } else {
                    null
                },
                annotations = mutableListOf(),
                comment = null,
                external = true,
                abstract = kind == InterfaceKind.ABSTRACT_CLASS
        )
    }
    val getterModels = getters.map { it.processAsTopLevel(declaration.name) }
    val setterModels = setters.map { it.processAsTopLevel(declaration.name) }
    return listOf(declaration) + getterModels + setterModels
}

fun IDLDictionaryMemberDeclaration.convertToParameterModel(): ParameterModel {
    return ParameterModel(
            name = name,
            type = type.toNullable().changeComment(null).process(),
            initializer = if (defaultValue != null && !required) {
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
            members = members.filterNot { it.inherited }.mapNotNull { it.process() },
            companionObject = null,
            typeParameters = listOf(),
            parentEntities = parents.map {
                HeritageModel(
                        it.process(),
                        listOf(),
                        null
                )
            },
            comment = null,
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
            comment = SimpleCommentModel(
                    "please, don't implement this interface!"
            ),
            annotations = mutableListOf(
                    AnnotationModel(
                            "Suppress",
                            listOf(IdentifierEntity("NESTED_CLASS_IN_EXTERNAL_INTERFACE"))
                    )
            ),
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
                                        left = IdentifierEntity(memberName),
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

fun IDLNamespaceDeclaration.convertToModel() : TopLevelModel {
    return ObjectModel(
            name = IdentifierEntity(name),
            members = attributes.mapNotNull { it.process() } +
                    operations.mapNotNull { it.process() },
            parentEntities = listOf()
    )
}

fun IDLTopLevelDeclaration.convertToModel(): List<TopLevelModel>? {
    return when (this) {
        is IDLInterfaceDeclaration -> convertToModel()
        is IDLDictionaryDeclaration -> convertToModel()
        is IDLEnumDeclaration -> convertToModel()
        is IDLNamespaceDeclaration -> listOf(convertToModel())
        is IDLTypedefDeclaration -> null
        is IDLImplementsStatementDeclaration -> null
        is IDLIncludesStatementDeclaration -> null
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
                open = open
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
        is IDLGetterDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = listOf(key.process()),
                type = valueType.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                operator = false,
                annotations = listOf(),
                open = false
        )
        is IDLSetterDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = listOf(key.process(), value.process()),
                type = TypeValueModel(
                        value = IdentifierEntity("Unit"),
                        params = listOf(),
                        metaDescription = null
                ),
                typeParameters = listOf(),
                static = false,
                override = false,
                operator = false,
                annotations = listOf(),
                open = false
        )
        else -> raiseConcern("unprocessed member declaration: ${this}") { null }
    }
}

fun IDLFileDeclaration.process(): SourceFileModel {
    val modelsExceptEnumsAndGenerated = declarations.filterNot {
        it is IDLEnumDeclaration || (it is IDLInterfaceDeclaration && it.generated)
    }.mapNotNull { it.convertToModel() }.flatten()

    val enumModels = declarations.filterIsInstance<IDLEnumDeclaration>().map { it.convertToModel() }.flatten()

    val generatedModels = declarations.filter {
        it is IDLInterfaceDeclaration && it.generated
    }.mapNotNull { it.convertToModel() }.flatten()

    val module = ModuleModel(
            name = packageName ?: ROOT_PACKAGENAME,
            shortName = packageName?.rightMost() ?: ROOT_PACKAGENAME,
            declarations = modelsExceptEnumsAndGenerated + generatedModels + enumModels,
            annotations = mutableListOf(),
            submodules = listOf(),
            imports = mutableListOf("kotlin.js.*".toNameEntity())
    )

    return SourceFileModel(
            name = null,
            fileName = File(fileName).normalize().absolutePath,
            root = module,
            referencedFiles = referencedFiles
    )
}

fun IDLSourceSetDeclaration.process(): SourceSetModel {
    return SourceSetModel(
            "<IRRELEVANT>",
            sources = files.map { it.process() }
    )
}
