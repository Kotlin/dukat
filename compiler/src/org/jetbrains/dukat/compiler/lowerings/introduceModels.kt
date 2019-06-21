package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.ast.model.nodes.processing.rightMost
import org.jetbrains.dukat.ast.model.nodes.processing.toNode
import org.jetbrains.dukat.ast.model.nodes.processing.translate
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.lowerings.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import java.io.File


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
    FUNCTION_TYPE
}

private data class Members(
        val dynamic: List<MemberNode>,
        val static: List<MemberNode>
)

private fun split(members: List<MemberNode>): Members {
    val staticMembers = mutableListOf<MemberNode>()
    val ownMembers = mutableListOf<MemberNode>()

    members.forEach { member ->
        if (member.isStatic()) {
            staticMembers.add(member.process())
        } else ownMembers.add(member.process())
    }

    return Members(ownMembers, staticMembers)
}

private fun MemberNode.process(): MemberNode {
    // TODO: how ClassModel end up here?
    return when (this) {
        is ConstructorNode -> ConstructorModel(
                parameters = parameters.map { param -> param.process() },
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            name = typeParam.value,
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                generated = generated
        )
        is ClassModel -> copy(members = members.map { member -> member.process() })
        is MethodNode -> MethodModel(
                name = name,
                parameters = parameters.map { param -> param.process() },
                type = type.process(),
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            name = typeParam.value,
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },

                static = static,

                override = override,
                operator = operator,
                annotations = annotations,

                open = open
        )
        is PropertyNode -> PropertyModel(
                name = name,
                type = type.process(),
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            name = typeParam.value,
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                static = static,
                override = override,
                getter = getter,
                setter = setter,
                open = open
        )
        else -> this
    }
}

private fun ParameterNode.process(context: TranslationContext = TranslationContext.IRRELEVANT): ParameterModel {
    return ParameterModel(
            type = type.process(context),
            name = name,
            initializer = initializer?.let { valueNode ->
                // TODO: don't like this particular cast
                TypeValueModel(valueNode.value, emptyList(), meta, valueNode.nullable)
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
                        params.map { param -> param.process() },
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
                    IdentifierEntity(name),
                    typeParameters.map { typeParam -> TypeValueModel(typeParam.name.toNode(), emptyList(), null) },
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

private fun ClassNode.convertToClassModel(): TopLevelNode {
    val membersSplitted = split(members)

    return ClassModel(
            name = IdentifierEntity(name),
            members = membersSplitted.dynamic,
            companionObject = CompanionObjectModel(
                    "",
                    membersSplitted.static,
                    emptyList()
            ),
            primaryConstructor = if (primaryConstructor != null) {
                ConstructorModel(
                        parameters = primaryConstructor!!.parameters.map { param -> param.process() },
                        typeParameters = primaryConstructor!!.typeParameters.map { typeParam ->
                            TypeParameterModel(
                                    name = typeParam.value,
                                    constraints = typeParam.params.map { param -> param.process() }
                            )
                        },
                        generated = primaryConstructor!!.generated
                )
            } else null,
            typeParameters = typeParameters.map { typeParam ->
                TypeParameterModel(
                        name = typeParam.value,
                        constraints = typeParam.params.map { param -> param.process() }
                )
            },
            parentEntities = parentEntities.map { parentEntity -> parentEntity.convertToModel() },
            annotations = exportQualifier.toAnnotation()
    )
}

private fun NameEntity.toTypeValueModel(): TypeValueModel {
    val nameTranslated = when (this) {
        is NameEntity -> translate()
        else -> raiseConcern("unknown HeritageSymbolNode ${this}") { this.toString() }
    }

    return TypeValueModel(IdentifierEntity(nameTranslated), emptyList(), null)
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
            name = IdentifierEntity(name),
            members = membersSplitted.dynamic,
            companionObject = CompanionObjectModel(
                    "",
                    membersSplitted.static,
                    emptyList()
            ),
            typeParameters = typeParameters.map { typeParam ->
                TypeParameterModel(
                        name = typeParam.value,
                        constraints = typeParam.params.map { param -> param.process() }
                )
            },
            parentEntities = parentEntities.map { parentEntity -> parentEntity.convertToModel() },
            annotations = exportQualifier.toAnnotation()
    )
}

private fun ExportQualifier?.toAnnotation(): MutableList<AnnotationNode> {
    return when (this) {
        is JsModule -> mutableListOf(AnnotationNode("JsModule", listOf(name)))
        is JsDefault -> mutableListOf(AnnotationNode("JsName", listOf(IdentifierEntity("default"))))
        else -> mutableListOf()
    }
}

private fun TopLevelEntity.convertToModel(): TopLevelNode? {
    return when (this) {
        is ClassNode -> convertToClassModel()
        is InterfaceNode -> convertToInterfaceModel()
        is FunctionNode -> FunctionModel(
                name = name,
                parameters = parameters.map { param -> param.process() },
                type = type.process(),

                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            name = typeParam.value,
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                },
                generatedReferenceNodes = generatedReferenceNodes,
                annotations = exportQualifier.toAnnotation(),
                export = export,
                inline = inline,
                operator = operator,
                body = body
        )
        is EnumNode -> this
        is VariableNode -> VariableModel(
                name = name,
                type = type.process(),
                annotations = exportQualifier.toAnnotation(),
                immutable = immutable,
                inline = inline,
                initializer = initializer,
                get = get,
                set = set,
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            name = typeParam.value,
                            constraints = typeParam.params.map { param -> param.process() }
                    )
                }
        )
        is ObjectNode -> ObjectModel(
                name = IdentifierEntity(name),
                members = members.map { member -> member.process() },
                parentEntities = parentEntities.map { parentEntity -> parentEntity.convertToModel() }
        )
        is TypeAliasNode -> if (canBeTranslated) {
            TypeAliasModel(
                    name = name,
                    typeReference = typeReference.process(),
                    typeParameters = typeParameters.map { typeParameter -> TypeParameterModel(typeParameter, emptyList()) })
        } else null
        else -> {
            println("skipping ${this::class.simpleName}")
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

    val declarationsFiltered = mutableListOf<TopLevelNode>()
    val submodules = mutableListOf<ModuleModel>()
    declarationsMapped.forEach { declaration ->
        if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
    }

    val annotations = mutableListOf<AnnotationNode>()

    jsModule?.let {
        annotations.add(AnnotationNode("file:JsModule", listOf(it)))
    }

    jsQualifier?.let {
        annotations.add(AnnotationNode("file:JsQualifier", listOf(it)))
    }

    val module = ModuleModel(
            name = qualifiedPackageName,
            shortName = qualifiedPackageName.rightMost(),
            declarations = declarationsFiltered,
            annotations = annotations,
            sumbodules = submodules,
            imports = mutableListOf()
    )

    val isJsModule = (jsModule != null) || (jsQualifier != null)

    return if (isJsModule) {
        val (aliases, ownDeclarations) = module.declarations.partition { it is TypeAliasModel }
        if (aliases.isEmpty()) {
            module
        } else {

            generated.add(SourceFileModel(
                name = "types",
                fileName = sourceFileName,
                root = ModuleModel(
                    name = module.name,
                    shortName = module.shortName,
                    declarations = aliases,
                    annotations = mutableListOf(),
                    sumbodules = mutableListOf(),
                    imports = mutableListOf()
                ),
                referencedFiles = emptyList()
            ))

            module.copy(declarations = ownDeclarations)
        }
    } else {
        module
    }
}

fun SourceSetNode.introduceModels() = SourceSetModel(
        sources = sources.flatMap { source ->
            val rootFile = File(source.fileName)
            val fileName = rootFile.normalize().absolutePath

            val generated = mutableListOf<SourceFileModel>()
            val root = source.root.introduceModels(source.fileName, generated)

            val module = SourceFileModel(
                    name = null,
                    fileName = fileName,
                    root = root,
                    referencedFiles = source.referencedFiles.map { referenceFile ->
                        val absolutePath = rootFile.resolveSibling(referenceFile.value).normalize().absolutePath
                        IdentifierEntity(absolutePath)
                    })

            generated + listOf(module)
        }
)