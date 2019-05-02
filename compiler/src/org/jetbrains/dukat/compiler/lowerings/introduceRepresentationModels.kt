package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.ValueTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.MuteMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.lowerings.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
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
                            name = typeParam.name,
                            constraints = typeParam.constraints.map { param -> param.process() }
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
                            name = typeParam.name,
                            constraints = typeParam.constraints.map { param -> param.process() }
                    )
                },

                static = static,

                override = override,
                operator = operator,
                annotations = annotations,

                open = open,
                definedExternally = definedExternally
        )
        is PropertyNode -> PropertyModel(
                name = name,
                type = type.process(),
                typeParameters = typeParameters.map { typeParam ->
                    TypeParameterModel(
                            name = typeParam.name,
                            constraints = typeParam.constraints.map { param -> param.process() }
                    )
                },
                static = static,
                override = override,
                getter = getter,
                setter = setter,
                open = open,
                definedExternally = definedExternally
        )
        else -> this
    }
}

private fun ParameterDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): ParameterModel {
    return ParameterModel(
            type = type.process(context),
            name = name,
            initializer = initializer,
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

private fun ParameterValueDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): TypeNode {
    return when (this) {
        is UnionTypeNode -> TypeValueModel(
                IdentifierNode("dynamic"),
                emptyList(),
                params.map { it.process().translate() }.joinToString(" | ")
        )
        is TupleDeclaration -> TypeValueModel(
                IdentifierNode("dynamic"),
                emptyList(),
                "JsTuple<${params.map { it.process().translate() }.joinToString(", ")}>"
        )
        is ValueTypeNode -> {
            if ((value == IdentifierNode("String")) && (meta is StringTypeDeclaration)) {
                TypeValueModel(value as NameNode, emptyList(), (meta as StringTypeDeclaration).tokens.joinToString("|"))
            } else {
                TypeValueModel(
                        value as NameNode,
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
                    IdentifierNode(name),
                    typeParameters.map { typeParam -> TypeValueModel(IdentifierNode(typeParam.name), emptyList(), null) },
                    meta?.processMeta(nullable, setOf(MetaDataOptions.SKIP_NULLS)),
                    nullable
            )

        }
        is QualifiedNode -> this
        else -> throw Exception("unable to process ParameterValueDeclaration ${this}")
    }
}

private fun ClassNode.convertToClassModel(): TopLevelNode {
    val membersSplitted = split(members)

    return ClassModel(
            name = name,
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
                                    name = typeParam.name,
                                    constraints = typeParam.constraints.map { param -> param.process() }
                            )
                        },
                        generated = primaryConstructor!!.generated
                )
            } else null,
            typeParameters = typeParameters.map { typeParam ->
                TypeParameterModel(
                        name = typeParam.name,
                        constraints = typeParam.constraints.map { param -> param.process() }
                )
            },
            parentEntities = parentEntities,
            annotations = annotations
    )
}

private fun InterfaceNode.convertToInterfaceModel(): InterfaceModel {
    val membersSplitted = split(members)

    return InterfaceModel(
            name = name,
            members = membersSplitted.dynamic,
            companionObject = CompanionObjectModel(
                    "",
                    membersSplitted.static,
                    emptyList()
            ),
            typeParameters = typeParameters.map { typeParam ->
                TypeParameterModel(
                        name = typeParam.name,
                        constraints = typeParam.constraints.map { param -> param.process() }
                )
            },
            parentEntities = parentEntities,
            annotations = annotations
    )
}

fun DocumentRootNode.introduceRepresentationModels(): ModuleModel {
    val declarations = declarations.mapNotNull { declaration ->
        when (declaration) {
            is DocumentRootNode -> declaration.introduceRepresentationModels()
            is ClassNode -> declaration.convertToClassModel()
            is InterfaceNode -> declaration.convertToInterfaceModel()
            is FunctionNode -> FunctionModel(
                    name = declaration.name,
                    parameters = declaration.parameters.map { param -> param.process() },
                    type = declaration.type.process(),

                    typeParameters = declaration.typeParameters.map { typeParam ->
                        TypeParameterModel(
                                name = typeParam.name,
                                constraints = typeParam.constraints.map { param -> param.process() }
                        )
                    },
                    generatedReferenceNodes = declaration.generatedReferenceNodes,
                    annotations = declaration.annotations,
                    export = declaration.export,
                    inline = declaration.inline,
                    operator = declaration.operator,
                    body = declaration.body
            )
            is EnumNode -> declaration
            is VariableNode -> VariableModel(
                    name = declaration.name,
                    type = declaration.type.process(),
                    annotations = declaration.annotations,
                    immutable = declaration.immutable,
                    inline = declaration.inline,
                    initializer = declaration.initializer,
                    get = declaration.get,
                    set = declaration.set,
                    typeParameters = declaration.typeParameters.map { typeParam ->
                        TypeParameterModel(
                                name = typeParam.name,
                                constraints = typeParam.constraints.map { param -> param.process() }
                        )
                    }
            )
            is ObjectNode -> ObjectModel(
                    name = declaration.name,
                    members = declaration.members.map { member -> member.process() },
                    parentEntities = declaration.parentEntities
            )
            else -> {
                println("skipping ${declaration::class.simpleName}")
                null
            }
        }
    }


    val declarationsFiltered = mutableListOf<TopLevelNode>()
    val submodules = mutableListOf<ModuleModel>()
    declarations.forEach { declaration ->
        if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
    }

    val annotations = mutableListOf<AnnotationNode>()

    if (showQualifierAnnotation) {
        if (qualifiedNode != null) {
            val qualifier = if (isQualifier) "JsQualifier" else "JsModule"
            annotations.add(AnnotationNode("file:${qualifier}", listOf(qualifiedNode!!)))
        }
    }

    return ModuleModel(
            qualifiedNode,
            packageName = fullPackageName,
            shortName = packageName,
            declarations = declarationsFiltered,
            annotations = annotations,
            sumbodules = submodules,
            imports = mutableListOf()
    )
}

fun SourceSetNode.introduceRepresentationModels() = SourceSetModel(
        sources = sources.map { source ->
            val rootFile = File(source.fileName)
            val fileName = rootFile.normalize().absolutePath
            SourceFileModel(fileName, source.root.introduceRepresentationModels(), source.referencedFiles.map { referenceFile ->
                val absolutePath = rootFile.resolveSibling(referenceFile.value).normalize().absolutePath
                IdentifierNode(absolutePath)
            })
        }
)