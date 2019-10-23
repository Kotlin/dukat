package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.nodes.ClassLikeReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionFromCallSignature
import org.jetbrains.dukat.ast.model.nodes.FunctionFromMethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureGetter
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureSetter
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.isUnit
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.EnumTokenModel
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
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.ChainCallModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementCallModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import java.io.File

private val logger = Logging.logger("introduceModels")

private enum class MetaDataOptions {
    SKIP_NULLS
}

private fun MemberNode.isStatic() = when (this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}


private enum class TranslationContext {
    IRRELEVANT,
    FUNCTION_TYPE,
    CONSTRUCTOR
}

private data class Members(
        val dynamic: List<MemberModel>,
        val static: List<MemberModel>
)

private fun split(members: List<MemberNode>): Members {
    val staticMembers = mutableListOf<MemberModel>()
    val ownMembers = mutableListOf<MemberModel>()

    members.forEach { member ->
        val memberProcessed = member.process()
        if (memberProcessed != null) {
            if (member.isStatic()) {
                staticMembers.add(memberProcessed)
            } else ownMembers.add(memberProcessed)
        }
    }

    return Members(ownMembers, staticMembers)
}

private fun MethodNode.resolveAnnotations(): List<AnnotationModel> {
    if (operator) {
        return when (name) {
            "get" -> listOf(AnnotationModel("nativeGetter", emptyList()))
            "set" -> listOf(AnnotationModel("nativeSetter", emptyList()))
            "invoke" -> listOf(AnnotationModel("nativeInvoke", emptyList()))
            else -> emptyList()
        }
    }

    return emptyList()
}

private fun MemberNode.process(): MemberModel? {
    // TODO: how ClassModel end up here?
    return when (this) {
        is ConstructorNode -> ConstructorModel(
                parameters = parameters.map { param -> param.process(TranslationContext.CONSTRUCTOR) },
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            type = TypeValueModel(typeParam.value, listOf(), null),
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                generated = generated
        )
        is MethodNode -> MethodModel(
                name = IdentifierEntity(name),
                parameters = parameters.map { param -> param.process() },
                type = type.process(),
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            type = TypeValueModel(typeParam.value, listOf(), null),
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },

                static = static,

                override = override,
                operator = operator,
                annotations = resolveAnnotations(),

                open = open
        )
        is PropertyNode -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.process(),
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            type = TypeValueModel(typeParam.value, listOf(), null),
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                static = static,
                override = override,
                getter = getter,
                setter = setter,
                open = open
        )
        else -> raiseConcern("unprocessed MemberNode: ${this}") { null }
    }
}

private fun ParameterNode.process(context: TranslationContext = TranslationContext.IRRELEVANT): ParameterModel {
    return ParameterModel(
            type = type.process(context),
            name = name,
            initializer = if (context == TranslationContext.CONSTRUCTOR) {
                null
            } else {
                initializer?.let { valueNode ->
                    // TODO: don't like this particular cast
                    StatementCallModel(valueNode.value, null, listOf(), meta)
                }
            },
            vararg = vararg,
            optional = optional
    )
}

private fun ParameterValueDeclaration?.processMeta(ownerIsNullable: Boolean, metadataOptions: Set<MetaDataOptions> = emptySet()): String? {
    return when (this) {
        is ThisTypeInGeneratedInterfaceMetaData -> "this"
        is IntersectionMetadata -> params.map {
            it.process().translate()
        }.joinToString(" & ")
        else -> {
            if (!metadataOptions.contains(MetaDataOptions.SKIP_NULLS)) {
                val skipNullableAnnotation = this is MuteMetadata
                if (ownerIsNullable && !skipNullableAnnotation) {
                    //TODO: consider rethinking this restriction
                    return "= null"
                } else null
            } else null
        }
    }
}

private fun TranslationContext.resolveAsMetaOptions(): Set<MetaDataOptions> {
    return if (this == TranslationContext.FUNCTION_TYPE) {
        setOf()
    } else {
        setOf(MetaDataOptions.SKIP_NULLS)
    }
}


private fun ParameterValueDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): TypeModel {
    return when (this) {
        is UnionTypeNode -> TypeValueModel(
                IdentifierEntity("dynamic"),
                emptyList(),
                params.map { unionMember ->
                    if (unionMember.meta is StringLiteralDeclaration) {
                        (unionMember.meta as StringLiteralDeclaration).token
                    } else {
                        unionMember.process().translate()
                    }
                }.joinToString(" | ")
        )
        is TupleTypeNode -> TypeValueModel(
                IdentifierEntity("dynamic"),
                emptyList(),
                "JsTuple<${params.map { it.process().translate() }.joinToString(", ")}>"
        )
        is TypeValueNode -> {
            if ((value == IdentifierEntity("String")) && (meta is StringLiteralDeclaration)) {
                TypeValueModel(value, emptyList(), (meta as StringLiteralDeclaration).token)
            } else {
                TypeValueModel(
                        value,
                        params.map { param -> param.process() }.map { TypeParameterModel(it, listOf()) },
                        meta.processMeta(nullable, context.resolveAsMetaOptions()),
                        nullable
                )
            }
        }
        is FunctionTypeNode -> {
            FunctionTypeModel(
                    parameters = (parameters.map { param ->
                        param.process(TranslationContext.FUNCTION_TYPE)
                    }),
                    type = type.process(TranslationContext.FUNCTION_TYPE),
                    metaDescription = meta.processMeta(nullable, context.resolveAsMetaOptions()),
                    nullable = nullable
            )
        }
        is GeneratedInterfaceReferenceDeclaration -> {
            TypeValueModel(
                    name,
                    typeParameters.map { typeParam -> TypeValueModel(typeParam.name, emptyList(), null) }
                            .map { TypeParameterModel(it, listOf()) },
                    meta?.processMeta(nullable, setOf(MetaDataOptions.SKIP_NULLS)),
                    nullable
            )

        }
        else -> raiseConcern("unable to process ParameterValueDeclaration ${this}") {
            TypeValueModel(
                    IdentifierEntity("dynamic"),
                    emptyList(),
                    null,
                    false
            )
        }
    }
}

private fun ClassNode.convertToClassModel(): TopLevelModel {
    val membersSplitted = split(members)

    return ClassModel(
            name = name,
            members = membersSplitted.dynamic,
            companionObject = if (membersSplitted.static.isNotEmpty()) {
                ObjectModel(
                        IdentifierEntity(""),
                        membersSplitted.static,
                        emptyList(),
                        VisibilityModifierModel.DEFAULT,
                        null
                )
            } else {
                null
            },
            primaryConstructor = if (primaryConstructor != null) {
                ConstructorModel(
                        parameters = primaryConstructor!!.parameters.map { param -> param.process() },
                        typeParameters = primaryConstructor!!.typeParameters.map { typeParam ->
                            TypeParameterModel(
                                    type = TypeValueModel(typeParam.value, listOf(), null),
                                    constraints = typeParam.params.map { param -> param.process() }
                            )
                        },
                        generated = primaryConstructor!!.generated
                )
            } else null,
            typeParameters = typeParameters.map { typeParam ->
                TypeParameterModel(
                        type = TypeValueModel(typeParam.value, listOf(), null),
                        constraints = typeParam.params.map { param -> param.process() }
                )
            },
            parentEntities = parentEntities.map { parentEntity -> parentEntity.convertToModel() },
            annotations = exportQualifier.toAnnotation(),
            comment = null,
            external = true,
            abstract = false,
            visibilityModifier = VisibilityModifierModel.DEFAULT
    )
}

private fun NameEntity.toTypeValueModel(): TypeValueModel {
    return TypeValueModel(this, emptyList(), null)
}

private fun HeritageNode.convertToModel(): HeritageModel {
    return HeritageModel(
            value = name.toTypeValueModel(),
            typeParams = typeArguments.map { typeArgument -> typeArgument.process() },
            delegateTo = null
    )
}

private fun InterfaceNode.convertToInterfaceModel(): InterfaceModel {
    val membersSplitted = split(members)

    return InterfaceModel(
            name = name,
            members = membersSplitted.dynamic,
            companionObject = if (membersSplitted.static.isNotEmpty()) {
                ObjectModel(
                        IdentifierEntity(""),
                        membersSplitted.static,
                        emptyList(),
                        VisibilityModifierModel.DEFAULT,
                        null
                )
            } else {
                null
            },
            typeParameters = typeParameters.map { typeParam ->
                TypeParameterModel(
                        type = TypeValueModel(typeParam.value, listOf(), null),
                        constraints = typeParam.params.map { param -> param.process() }
                )
            },
            parentEntities = parentEntities.map { parentEntity -> parentEntity.convertToModel() },
            annotations = exportQualifier.toAnnotation(),
            comment = null,
            external = true,
            visibilityModifier = VisibilityModifierModel.DEFAULT
    )
}

private fun ExportQualifier?.toAnnotation(): MutableList<AnnotationModel> {
    return when (this) {
        is JsModule -> mutableListOf(AnnotationModel("JsModule", listOf(name)))
        is JsDefault -> mutableListOf(AnnotationModel("JsName", listOf(IdentifierEntity("default"))))
        else -> mutableListOf()
    }
}

private fun VariableNode.resolveGetter(): StatementModel? {
    return if (inline) {
        ChainCallModel(
                StatementCallModel(
                        QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()
                ),
                StatementCallModel(name.rightMost(), null)
        )
    } else null
}

private fun VariableNode.resolveSetter(): StatementModel? {
    return if (inline) {
        AssignmentStatementModel(
                ChainCallModel(
                        StatementCallModel(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                        StatementCallModel(name.rightMost(), null)
                ),
                StatementCallModel(IdentifierEntity("value"), null)
        )
    } else null
}

private fun FunctionNode.resolveBody(): List<StatementModel> {
    val nodeContext = this.context
    return when (nodeContext) {
        is IndexSignatureGetter -> listOf(
                ReturnStatementModel(
                        ChainCallModel(
                                StatementCallModel(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                                StatementCallModel(IdentifierEntity("get"), listOf(
                                        IdentifierEntity(nodeContext.name)
                                ))
                        )
                )
        )

        is IndexSignatureSetter -> listOf(ChainCallModel(
                StatementCallModel(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                StatementCallModel(IdentifierEntity("set"), listOf(
                        IdentifierEntity(nodeContext.name),
                        IdentifierEntity("value")
                )))
        )

        is FunctionFromCallSignature -> listOf(ChainCallModel(
                StatementCallModel(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                StatementCallModel(IdentifierEntity("invoke"), nodeContext.params))
        )

        is FunctionFromMethodSignatureDeclaration -> {
            val bodyStatement = ChainCallModel(
                    StatementCallModel(
                            QualifierEntity(
                                    IdentifierEntity("this"),
                                    IdentifierEntity("asDynamic")
                            )
                            , emptyList()),
                    StatementCallModel(IdentifierEntity(nodeContext.name), nodeContext.params)
            )

            listOf(if (type.isUnit()) {
                bodyStatement
            } else {
                ReturnStatementModel(bodyStatement)
            })
        }
        else -> emptyList()
    }
}

private fun ClassLikeReferenceNode?.convert(): ClassLikeReferenceModel? {
    return this?.let { extendNode ->
        ClassLikeReferenceModel(extendNode.name, extendNode.typeParameters)
    }
}

fun TopLevelEntity.convertToModel(): TopLevelModel? {
    return when (this) {
        is ClassNode -> convertToClassModel()
        is InterfaceNode -> convertToInterfaceModel()
        is EnumNode -> {
            EnumModel(
                    name = name,
                    values = values.map { token -> EnumTokenModel(token.value, token.meta) },
                    visibilityModifier = VisibilityModifierModel.DEFAULT,
                    comment = null
            )
        }
        is FunctionNode -> FunctionModel(
                name = name,
                parameters = parameters.map { param -> param.process() },
                type = type.process(),

                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            type = TypeValueModel(typeParam.value, listOf(), null),
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                annotations = exportQualifier.toAnnotation(),
                export = export,
                inline = inline,
                operator = operator,
                extend = extend.convert(),
                body = resolveBody(),
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = comment
        )
        is VariableNode -> VariableModel(
                name = name,
                type = type.process(),
                annotations = exportQualifier.toAnnotation(),
                immutable = immutable,
                inline = inline,
                initializer = null,
                get = resolveGetter(),
                set = resolveSetter(),
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            type = TypeValueModel(typeParam.value, listOf(), null),
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                extend = extend.convert(),
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null
        )
        is ObjectNode -> ObjectModel(
                name = name,
                members = members.mapNotNull { member -> member.process() },
                parentEntities = parentEntities.map { parentEntity -> parentEntity.convertToModel() },
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                comment = null
        )
        is TypeAliasNode -> if (canBeTranslated) {
            TypeAliasModel(
                    name = name,
                    typeReference = typeReference.process(),
                    typeParameters = typeParameters.map { typeParameter -> TypeParameterModel(TypeValueModel(typeParameter, listOf(), null), emptyList()) },
                    visibilityModifier = VisibilityModifierModel.DEFAULT,
                    comment = null
            )
        } else null
        else -> {
            logger.debug("skipping ${this}")
            null
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun DocumentRootNode.introduceModels(sourceFileName: String, generated: MutableList<SourceFileModel>): ModuleModel {
    val (roots, topDeclarations) = declarations.partition { it is DocumentRootNode }

    val declarationsMapped = (roots as List<DocumentRootNode>).map { it.introduceModels(sourceFileName, generated) } + topDeclarations.mapNotNull { declaration ->
        declaration.convertToModel()
    }

    val declarationsFiltered = mutableListOf<TopLevelModel>()
    val submodules = mutableListOf<ModuleModel>()
    declarationsMapped.forEach { declaration ->
        if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
    }

    val annotations = mutableListOf<AnnotationModel>()

    jsModule?.let {
        annotations.add(AnnotationModel("file:JsModule", listOf(it)))
    }

    jsQualifier?.let {
        annotations.add(AnnotationModel("file:JsQualifier", listOf(it)))
    }

    val module = ModuleModel(
            name = qualifiedPackageName,
            shortName = qualifiedPackageName.rightMost(),
            declarations = declarationsFiltered,
            annotations = annotations,
            submodules = submodules,
            imports = mutableListOf(),
            comment = null
    )

    return module
}

fun SourceSetNode.introduceModels() = SourceSetModel(
        sourceName = sourceName,
        sources = sources.flatMap { source ->
            val rootFile = File(source.fileName)
            val fileName = rootFile.normalize().absolutePath

            val generated = mutableListOf<SourceFileModel>()
            val root = source.root.introduceModels(source.fileName, generated)

            val module = SourceFileModel(
                    name = source.name,
                    fileName = fileName,
                    root = root,
                    referencedFiles = source.referencedFiles.map { referenceFile ->
                        val absolutePath = rootFile.resolveSibling(referenceFile.value).normalize().absolutePath
                        absolutePath
                    })

            generated + listOf(module)
        }
)