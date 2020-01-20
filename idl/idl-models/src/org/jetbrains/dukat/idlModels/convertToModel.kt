package org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.*
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.*
import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.idlLowerings.IDLLowering
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

private fun IDLDeclaration.resolveName(): String? {
    return when (this) {
        is IDLDictionaryDeclaration -> name
        is IDLEnumDeclaration -> name
        is IDLInterfaceDeclaration -> name
        is IDLTypedefDeclaration -> name
        is IDLSingleTypeDeclaration -> name
        else -> null
    }
}


private class IdlFileConverter(
    private val fileDeclaration: IDLFileDeclaration,
    private val typeMap: Map<String, NameEntity?>
) {

    private companion object {
        val stdLibTypes = setOf(
            "Any",
            "Array",
            "Boolean",
            "String",
            "Int",
            "Float",
            "Double",
            "Short",
            "Number",
            "dynamic",
            "Promise",
            "Unit",

            "ItemArrayLike"
        )

        val toStdMap = mapOf(
            "ByteString" to "String",
            "CSSOMString" to "String",
            "DOMError" to "dynamic",
            "DOMString" to "String",
            "FrozenArray" to "Array",
            "Promise" to "Promise",
            "USVString" to "String",
            "\$Array" to "Array",
            "\$dynamic" to "dynamic",
            "any" to "Any",
            "boolean" to "Boolean",
            "byte" to "Byte",
            "double" to "Double",
            "float" to "Float",
            "long" to "Int",
            "longlong" to "Int",
            "object" to "dynamic",
            "octet" to "Byte",
            "record" to "dynamic",
            "sequence" to "Array",
            "short" to "Short",
            "unrestricteddouble" to "Double",
            "unrestrictedfloat" to "Float",
            "unsignedlong" to "Int",
            "unsignedlonglong" to "Number",
            "unsignedshort" to "Short",
            "void" to "Unit"
        )
    }

    private fun String.stdFqName(): NameEntity? {
        val name = toStdMap[this] ?: this
        return if (stdLibTypes.contains(name)) {
            QualifierEntity(IdentifierEntity("<LIBROOT>"), IdentifierEntity(name))
        } else {
            null
        }
    }

    private fun IDLDeclaration.toFqName(): NameEntity? {
        val name = resolveName() ?: return null
        return name.stdFqName() ?: typeMap[name]?.appendLeft(IdentifierEntity(name))
    }

    private fun String.toFqName(): NameEntity? {
        return stdFqName() ?: fileDeclaration.packageName?.appendLeft(IdentifierEntity(this))
    }

    private fun IDLSingleTypeDeclaration.convertToModel(): TypeValueModel {
        val resolvedName = toStdMap[name] ?: name

        val typeModel = TypeValueModel(
            value = IdentifierEntity(resolvedName),
            params = listOfNotNull(typeParameter?.convertToModel())
                .map { TypeParameterModel(it, listOf()) }
                .map {
                    if (name == "FrozenArray") {
                        it.copy(variance = Variance.COVARIANT)
                    } else {
                        it
                    }
                },
            metaDescription = comment,
            fqName = toFqName()
        )

        return typeModel.copy(
            nullable = when (typeModel.value) {
                IdentifierEntity("dynamic") -> false
                IdentifierEntity("Any") -> true
                else -> nullable
            }
        )
    }

    private fun IDLFunctionTypeDeclaration.convertToModel(): FunctionTypeModel {

        val returnTypeModel = if (returnType.name == "any") {
            TypeValueModel(IdentifierEntity("dynamic"), listOf(), null, null)
        } else {
            returnType.convertToModel()
        }

        return FunctionTypeModel(
            parameters = arguments.filterNot { it.variadic }.map { it.convertToModel() },
            type = returnTypeModel,
            metaDescription = comment,
            nullable = nullable
        )
    }

    private fun IDLTypeDeclaration.convertToModel(): TypeModel {
        return when (this) {
            is IDLSingleTypeDeclaration -> convertToModel()
            is IDLFunctionTypeDeclaration -> convertToModel()
            //there shouldn't be any UnionTypeDeclarations at this stage
            else -> raiseConcern("unprocessed type declaration: ${this}") {
                TypeValueModel(
                    IdentifierEntity("IMPOSSIBLE"),
                    listOf(),
                    null,
                    null
                )
            }
        }
    }

    private fun IDLArgumentDeclaration.convertToModel(): ParameterModel {
        return ParameterModel(
            name = name,
            type = type.convertToModel(),
            initializer = if (optional || defaultValue != null) {
                StatementCallModel(
                    IdentifierEntity("definedExternally"),
                    null
                )
            } else {
                null
            },
            vararg = variadic
        )
    }

    private fun IDLSetterDeclaration.processAsTopLevel(ownerName: NameEntity): FunctionModel {
        return FunctionModel(
            name = IdentifierEntity("set"),
            parameters = listOf(key.convertToModel(), value.convertToModel()),
            type = TypeValueModel(
                value = IdentifierEntity("Unit"),
                params = listOf(),
                metaDescription = null,
                fqName = "Unit".stdFqName()
            ),
            typeParameters = listOf(),
            annotations = mutableListOf(
                AnnotationModel(
                    name = "Suppress",
                    params = listOf(
                        IdentifierEntity("INVISIBLE_REFERENCE"),
                        IdentifierEntity("INVISIBLE_MEMBER")
                    )
                ),
                AnnotationModel(
                    name = "kotlin.internal.InlineOnly",
                    params = listOf()
                )
            ),
            export = false,
            inline = true,
            operator = true,
            extend = ClassLikeReferenceModel(
                name = ownerName,
                typeParameters = listOf()
            ),
            body = listOf(
                AssignmentStatementModel(
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
                )
            ),
            visibilityModifier = VisibilityModifierModel.DEFAULT,
            comment = null
        )
    }

    private fun IDLGetterDeclaration.processAsTopLevel(ownerName: NameEntity): FunctionModel {
        return FunctionModel(
            name = IdentifierEntity("get"),
            parameters = listOf(key.convertToModel()),
            type = valueType.toNullableIfNotPrimitive().convertToModel(),
            typeParameters = listOf(),
            annotations = mutableListOf(
                AnnotationModel(
                    name = "Suppress",
                    params = listOf(
                        IdentifierEntity("INVISIBLE_REFERENCE"),
                        IdentifierEntity("INVISIBLE_MEMBER")
                    )
                ),
                AnnotationModel(
                    name = "kotlin.internal.InlineOnly",
                    params = listOf()
                )
            ),
            export = false,
            inline = true,
            operator = true,
            extend = ClassLikeReferenceModel(
                name = ownerName,
                typeParameters = listOf()
            ),
            body = listOf(
                ReturnStatementModel(
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
                )
            ),
            visibilityModifier = VisibilityModifierModel.DEFAULT,
            comment = null
        )
    }

    private fun IDLInterfaceDeclaration.convertToModel(): List<TopLevelModel> {
        if (mixin) {
            return listOf()
        }

        val (staticAttributes, dynamicAttributes) = attributes.partition { it.static }
        val (staticOperations, dynamicOperations) = operations.partition { it.static }

        val dynamicMemberModels = (
                constructors +
                        dynamicAttributes + dynamicOperations +
                        getters.filterNot { it.name == "get" } +
                        setters.filterNot { it.name == "set" }
                ).mapNotNull {
            it.convertToModel()
        }.distinct()


        val staticMemberModels = (staticAttributes + staticOperations).mapNotNull {
            it.convertToModel()
        }.distinct()

        val companionObjectModel = if (staticMemberModels.isNotEmpty()) {
            ObjectModel(
                name = IdentifierEntity(""),
                members = staticMemberModels,
                parentEntities = listOf(),
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null
            )
        } else {
            null
        }

        val parentModels = (parents + unions).map {
            HeritageModel(
                it.convertToModel(),
                listOf(),
                null
            )
        }

        val annotationModels = listOfNotNull(
            if (companionObjectModel != null) {
                AnnotationModel(
                    "Suppress",
                    listOf(IdentifierEntity("NESTED_CLASS_IN_EXTERNAL_INTERFACE"))
                )
            } else {
                null
            }
        ).toMutableList()

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
                external = true,
                visibilityModifier = VisibilityModifierModel.DEFAULT
            )
        } else {
            ClassModel(
                name = IdentifierEntity(name),
                members = dynamicMemberModels,
                companionObject = companionObjectModel,
                typeParameters = listOf(),
                parentEntities = parentModels,
                primaryConstructor = if (primaryConstructor != null) {
                    primaryConstructor!!.convertToModel() as ConstructorModel
                } else {
                    null
                },
                annotations = mutableListOf(),
                comment = null,
                external = true,
                abstract = kind == InterfaceKind.ABSTRACT_CLASS,
                visibilityModifier = VisibilityModifierModel.DEFAULT
            )
        }
        val getterModels = getters.map { it.processAsTopLevel(declaration.name) }
        val setterModels = setters.map { it.processAsTopLevel(declaration.name) }
        return listOf(declaration) + getterModels + setterModels
    }

    private fun IDLDictionaryMemberDeclaration.convertToParameterModel(): ParameterModel {
        return ParameterModel(
            name = name,
            type = type.toNullable().changeComment(null).convertToModel(),
            initializer = if (defaultValue != null && !required) {
                StatementCallModel(
                    IdentifierEntity(defaultValue!!),
                    null
                )
            } else {
                null
            },
            vararg = false
        )
    }

    private fun IDLDictionaryMemberDeclaration.convertToAssignmentStatementModel(): AssignmentStatementModel {
        return AssignmentStatementModel(
            IndexStatementModel(
                StatementCallModel(
                    IdentifierEntity("o"),
                    null
                ),
                StatementCallModel(
                    IdentifierEntity("\"$name\""),
                    null
                )
            ),
            StatementCallModel(
                IdentifierEntity(name),
                null
            )
        )
    }

    fun IDLDictionaryDeclaration.generateFunctionBody(): List<StatementModel> {
        val functionBody: MutableList<StatementModel> = mutableListOf(
            AssignmentStatementModel(
                StatementCallModel(
                    IdentifierEntity("val o"),
                    null
                ),
                StatementCallModel(
                    IdentifierEntity("js"),
                    listOf(IdentifierEntity("\"({})\""))
                )
            )
        )
        functionBody.addAll(members.map { it.convertToAssignmentStatementModel() })
        functionBody.add(
            ReturnStatementModel(
                StatementCallModel(
                    IdentifierEntity("o"),
                    null
                )
            )
        )
        return functionBody
    }

    private fun IDLDictionaryDeclaration.convertToModel(): List<TopLevelModel> {
        val declaration = InterfaceModel(
            name = IdentifierEntity(name),
            members = members.filterNot { it.inherited }.mapNotNull { it.convertToModel() },
            companionObject = null,
            typeParameters = listOf(),
            parentEntities = (parents + unions).map {
                HeritageModel(
                    it.convertToModel(),
                    listOf(),
                    null
                )
            },
            comment = null,
            annotations = mutableListOf(),
            external = true,
            visibilityModifier = VisibilityModifierModel.DEFAULT
        )
        val generatedFunction = FunctionModel(
            name = IdentifierEntity(name),
            parameters = members.map { it.convertToParameterModel() },
            type = TypeValueModel(
                value = IdentifierEntity(name),
                params = listOf(),
                metaDescription = null,
                fqName = toFqName()
            ),
            typeParameters = listOf(),
            annotations = mutableListOf(
                AnnotationModel(
                    name = "Suppress",
                    params = listOf(
                        IdentifierEntity("INVISIBLE_REFERENCE"),
                        IdentifierEntity("INVISIBLE_MEMBER")
                    )
                ),
                AnnotationModel(
                    name = "kotlin.internal.InlineOnly",
                    params = listOf()
                )
            ),
            export = false,
            inline = true,
            operator = false,
            extend = null,
            body = generateFunctionBody(),
            visibilityModifier = VisibilityModifierModel.DEFAULT,
            comment = null
        )
        return listOf(declaration, generatedFunction)
    }

    private fun IDLEnumDeclaration.convertToModel(): List<TopLevelModel> {
        val declaration = InterfaceModel(
            name = IdentifierEntity(name),
            members = listOf(),
            companionObject = ObjectModel(
                name = IdentifierEntity(""),
                members = listOf(),
                parentEntities = listOf(),
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null
            ),
            typeParameters = listOf(),
            parentEntities = unions.map {
                HeritageModel(
                    it.convertToModel(),
                    listOf(),
                    null
                )
            },
            comment = SimpleCommentEntity(
                "please, don't implement this interface!"
            ),
            annotations = mutableListOf(
                AnnotationModel(
                    "Suppress",
                    listOf(IdentifierEntity("NESTED_CLASS_IN_EXTERNAL_INTERFACE"))
                )
            ),
            external = true,
            visibilityModifier = VisibilityModifierModel.DEFAULT
        )
        val generatedVariables = members.map { memberName ->
            val processedName = processEnumMember(memberName)
            VariableModel(
                name = IdentifierEntity(processEnumMember(memberName)),
                type = TypeValueModel(
                    value = declaration.name,
                    params = listOf(),
                    metaDescription = null,
                    fqName = processedName.toFqName()
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
                ),
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null
            )
        }
        return listOf(declaration) + generatedVariables
    }

    private fun IDLNamespaceDeclaration.convertToModel(): TopLevelModel {
        return ObjectModel(
            name = IdentifierEntity(name),
            members = attributes.mapNotNull { it.convertToModel() } +
                    operations.mapNotNull { it.convertToModel() },
            parentEntities = listOf(),
            visibilityModifier = VisibilityModifierModel.DEFAULT,
            comment = null
        )
    }

    private fun IDLUnionDeclaration.convertToModel(): TopLevelModel {
        return InterfaceModel(
            name = IdentifierEntity(name),
            members = listOf(),
            companionObject = null,
            typeParameters = listOf(),
            parentEntities = unions.map {
                HeritageModel(
                    it.convertToModel(),
                    listOf(),
                    null
                )
            },
            comment = null,
            annotations = mutableListOf(),
            external = true,
            visibilityModifier = VisibilityModifierModel.DEFAULT
        )
    }

    private fun IDLTopLevelDeclaration.convertToModel(): List<TopLevelModel>? {
        return when (this) {
            is IDLInterfaceDeclaration -> convertToModel()
            is IDLDictionaryDeclaration -> convertToModel()
            is IDLEnumDeclaration -> convertToModel()
            is IDLNamespaceDeclaration -> listOf(convertToModel())
            is IDLTypedefDeclaration -> null
            is IDLImplementsStatementDeclaration -> null
            is IDLIncludesStatementDeclaration -> null
            is IDLUnionDeclaration -> listOf(convertToModel())
            else -> raiseConcern("unprocessed top level declaration: ${this}") { null }
        }
    }

    private fun IDLMemberDeclaration.convertToModel(): MemberModel? {
        return when (this) {
            is IDLAttributeDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.convertToModel(),
                typeParameters = listOf(),
                static = false,
                override = null,
                immutable = readOnly,
                getter = false,
                setter = false,
                open = open
            )
            is IDLOperationDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = arguments.map { it.convertToModel() },
                type = returnType.convertToModel(),
                typeParameters = listOf(),
                static = false,
                override = null,
                operator = false,
                annotations = listOf(),
                open = false
            )
            is IDLConstructorDeclaration -> ConstructorModel(
                parameters = arguments.map { it.convertToModel() },
                typeParameters = listOf(),
                generated = false
            )
            is IDLDictionaryMemberDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.toNullable().convertToModel(),
                typeParameters = listOf(),
                static = false,
                override = null,
                immutable = false,
                getter = false,
                setter = false,
                open = false
            )
            is IDLGetterDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = listOf(key.convertToModel()),
                type = valueType.convertToModel(),
                typeParameters = listOf(),
                static = false,
                override = null,
                operator = false,
                annotations = listOf(),
                open = false
            )
            is IDLSetterDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = listOf(key.convertToModel(), value.convertToModel()),
                type = TypeValueModel(
                    value = IdentifierEntity("Unit"),
                    params = listOf(),
                    metaDescription = null,
                    fqName = "Unit".stdFqName()
                ),
                typeParameters = listOf(),
                static = false,
                override = null,
                operator = false,
                annotations = listOf(),
                open = false
            )
            else -> raiseConcern("unprocessed member declaration: ${this}") { null }
        }
    }

    fun convert(): SourceFileModel {
        val modelsExceptEnumsAndGenerated = fileDeclaration.declarations.filterNot {
            it is IDLEnumDeclaration || (it is IDLInterfaceDeclaration && it.generated)
        }.mapNotNull { it.convertToModel() }.flatten()

        val enumModels =
            fileDeclaration.declarations.filterIsInstance<IDLEnumDeclaration>().map { it.convertToModel() }.flatten()

        val generatedModels = fileDeclaration.declarations.filter {
            it is IDLInterfaceDeclaration && it.generated
        }.mapNotNull { it.convertToModel() }.flatten()

        val module = ModuleModel(
            name = fileDeclaration.packageName ?: ROOT_PACKAGENAME,
            shortName = fileDeclaration.packageName?.rightMost() ?: ROOT_PACKAGENAME,
            declarations = modelsExceptEnumsAndGenerated + generatedModels + enumModels,
            annotations = mutableListOf(),
            submodules = listOf(),
            imports = mutableListOf(ImportModel("kotlin.js.*".toNameEntity())),
            comment = null
        )

        return SourceFileModel(
            name = null,
            fileName = File(fileDeclaration.fileName).normalize().absolutePath,
            root = module,
            referencedFiles = fileDeclaration.referencedFiles
        )

    }
}

private class IDLReferenceVisitor(private val visit: (IDLDeclaration, NameEntity?) -> Unit) : IDLLowering {
    override fun lowerTopLevelDeclaration(
        declaration: IDLTopLevelDeclaration,
        owner: IDLFileDeclaration
    ): IDLTopLevelDeclaration {
        visit(declaration, owner.packageName)
        return super.lowerTopLevelDeclaration(declaration, owner)
    }
}

fun IDLSourceSetDeclaration.convertToModel(): SourceSetModel {

    val typeMap = mutableMapOf<String, NameEntity?>()
    IDLReferenceVisitor { declaration, packageName ->
        declaration.resolveName()?.let {
            typeMap[it] = packageName ?: ROOT_PACKAGENAME
        }
    }.lowerSourceSetDeclaration(this)

    return SourceSetModel(
        "<IRRELEVANT>",
        sources = files.map { IdlFileConverter(it, typeMap).convert() }
    )
}