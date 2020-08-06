package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionFromCallSignature
import org.jetbrains.dukat.ast.model.nodes.FunctionFromMethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureGetter
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureSetter
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.LiteralUnionNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionLiteralKind
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.tsmodel.ExportQualifier
import org.jetbrains.dukat.tsmodel.JsDefault
import org.jetbrains.dukat.tsmodel.JsModule
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.astCommon.SimpleMetaData
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.isStringLiteral
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.EnumTokenModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.InitBlockModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.LambdaExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.modifiers.InheritanceModifierModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.stdlib.KLIBROOT
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceOriginDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import java.io.File

private val logger = Logging.logger("introduceModels")

private fun MemberNode.isStatic() = when (this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}

internal sealed class TranslationContext {
    object IRRELEVANT : TranslationContext()
    data class PROPERTY(val optional: Boolean) : TranslationContext()
}

private data class Members(
        val ownMembers: List<MemberModel>,
        val staticMembers: List<MemberModel>
)

private typealias UidMapper = Map<String, FqNode>

data class FqNode(val node: Entity, val fqName: NameEntity)

private fun dynamicType(metaDescription: String? = null) = TypeValueModel(
        IdentifierEntity("dynamic"),
        emptyList(),
        metaDescription,
        null
)

// TODO: duplication, think of separate place to have this (but please don't call it utils )))
private fun unquote(name: String): String {
    return name.replace("(?:^[\"|\'`])|(?:[\"|\'`]$)".toRegex(), "")
}

internal class DocumentConverter(
        private val moduleNode: ModuleNode,
        private val uidToNameMapper: UidMapper,
        private val exportQualifierMap: Map<String?, ExportQualifier>
) {
    private val imports = mutableListOf<ImportModel>()
    private val expressionConverter = ExpressionConverter { typeNode ->
        typeNode.process()
    }

    @Suppress("UNCHECKED_CAST")
    fun convert(sourceFileName: String, generated: MutableList<SourceFileModel>): ModuleModel {
        val (roots, topDeclarations) = moduleNode.declarations.partition { it is ModuleNode }

        val declarationsMapped = (roots as List<ModuleNode>).map { DocumentConverter(it, uidToNameMapper, exportQualifierMap).convert(sourceFileName, generated) } + topDeclarations.mapNotNull { declaration ->
            declaration.convertToModel(moduleNode)
        }

        val declarationsFiltered = mutableListOf<TopLevelModel>()
        val submodules = mutableListOf<ModuleModel>()
        declarationsMapped.forEach { declaration ->
            if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
        }

        val annotations = mutableListOf<AnnotationModel>()

        val exportQualifier = exportQualifierMap[moduleNode.uid]
        val jsModuleQualifier = exportQualifier as? JsModule

        jsModuleQualifier?.name?.let { qualifier ->
            annotations.add(AnnotationModel("file:JsModule", listOf(qualifier.process { unquote(it) })))
            annotations.add(AnnotationModel("file:JsNonModule", emptyList()))
        }

        jsModuleQualifier?.qualifier?.let { qualifier ->
            if (qualifier) {
                annotations.add(AnnotationModel("file:JsQualifier", listOf(moduleNode.qualifiedPackageName.process { unquote(it) })))
            }
        }

        return ModuleModel(
                name = moduleNode.qualifiedPackageName,
                shortName = moduleNode.qualifiedPackageName.rightMost(),
                declarations = declarationsFiltered,
                annotations = annotations,
                submodules = submodules,
                imports = imports,
                comment = null
        )
    }

    private fun ReferenceEntity.getFqName(): NameEntity? {
        return uidToNameMapper[uid]?.fqName
    }

    private fun TypeValueNode.getFqName(): NameEntity? {
        return typeReference?.getFqName() ?: if (KotlinStdlibEntities.contains(value)) {
            KLIBROOT.appendLeft(value)
        } else null
    }

    private fun ClassLikeNode.processMembers(): Members {
        val (staticNodes, ownNodes) = members.partition { it.isStatic() }
        return Members(ownNodes.flatMap { it.process() }, staticNodes.flatMap { it.process() })
    }

    private fun UnionTypeDeclaration.convertMeta(): String {
        return params.joinToString(" | ") { unionMember ->
            unionMember.process().translate()
        }
    }

    private fun HeritageNode.convertToModel(): HeritageModel {
        val isNamedImport = reference?.origin == ReferenceOriginDeclaration.NAMED_IMPORT
        if (isNamedImport) {
            reference?.getFqName()?.let { resolvedName ->
                imports.add(ImportModel(resolvedName, name.rightMost()))
            }
        }

        val fqName = if (isNamedImport) {
            name
        } else {
            reference?.getFqName()
        }
        return HeritageModel(
                value = TypeValueModel(name, emptyList(), null, fqName),
                typeParams = typeArguments.map { typeArgument -> typeArgument.process() },
                delegateTo = null
        )
    }

    private fun TypeModel.isDynamic(): Boolean {
        return (this is TypeValueModel) && (value == IdentifierEntity("dynamic"))
    }

    fun ParameterValueDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): TypeModel {
        val dynamicName = IdentifierEntity("dynamic")
        return when (this) {
            is LiteralUnionNode -> {
                val value = when (kind) {
                    UnionLiteralKind.NUMBER -> IdentifierEntity("Number")
                    else -> IdentifierEntity("String")
                }
                TypeValueModel(
                        value,
                        emptyList(),
                        params.joinToString(" | "),
                        KLIBROOT.appendLeft(value),
                        context == TranslationContext.PROPERTY(true)
                )
            }
            is UnionTypeDeclaration -> TypeValueModel(
                    dynamicName,
                    emptyList(),
                    convertMeta(),
                    null
            )
            is TupleDeclaration -> TypeValueModel(
                    dynamicName,
                    emptyList(),
                    "JsTuple<${params.map { it.process().translate() }.joinToString(", ")}>",
                    null
            )
            is TypeParamReferenceDeclaration -> {
                TypeParameterReferenceModel(
                        name = value,
                        metaDescription = meta.processMeta(),
                        nullable = nullable
                )
            }
            is TypeValueNode -> {
                val node = uidToNameMapper[typeReference?.uid]?.node
                if ((node is TypeAliasNode) && (node.typeReference is UnionTypeDeclaration)) {
                    dynamicType("typealias ${node.name} = dynamic")
                } else {
                    TypeValueModel(
                            value,
                            params.map { param -> param.process() }.map { TypeParameterModel(it, listOf()) },
                            meta.processMeta(),
                            getFqName(),
                            nullable
                    )
                }
            }
            is FunctionTypeNode -> {
                FunctionTypeModel(
                        parameters = (parameters.map { param ->
                            param.processAsLambdaParam()
                        }),
                        type = type.process(),
                        metaDescription = meta.processMeta(),
                        nullable = nullable
                )
            }
            is GeneratedInterfaceReferenceDeclaration -> {
                val typeParams = when (context) {
                    is TranslationContext.PROPERTY -> typeParameters.map {
                        TypeParameterModel(
                                type = TypeValueModel(
                                        IdentifierEntity("Any"),
                                        emptyList(),
                                        null,
                                        null,
                                        true
                                ),
                                constraints = emptyList()
                        )
                    }
                    else -> typeParameters.map { typeParam ->
                        TypeParameterModel(
                                type = typeParam.process(),
                                constraints = emptyList()
                        )
                    }
                }
                TypeValueModel(
                        name,
                        typeParams,
                        meta?.processMeta(),
                        typeReference?.getFqName(),
                        nullable
                )

            }
            else -> raiseConcern("unable to process TypeNode ${this}") {
                TypeValueModel(
                        IdentifierEntity("dynamic"),
                        emptyList(),
                        null,
                        null,
                        false
                )
            }
        }
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

    private fun MemberNode.process(): List<MemberModel> {
        // TODO: how ClassModel end up here?
        return when (this) {
            is ConstructorNode -> listOfNotNull(
                ConstructorModel(
                    parameters = parameters.map { param -> param.process().copy() },
                    typeParameters = convertTypeParams(typeParameters)
                ),
                body?.let {
                    InitBlockModel(
                        body = expressionConverter.convertBlock(it)
                    )
                }
            )
            is MethodNode -> listOf(process())
            is PropertyNode -> listOf(PropertyModel(
                    name = IdentifierEntity(name),
                    type = type.process(TranslationContext.PROPERTY(getter || setter)),
                    typeParameters = convertTypeParams(typeParameters),
                    static = static,
                    override = null,
                    immutable = getter && !setter,
                    initializer = expressionConverter.convertExpression(initializer),
                    getter = getter,
                    setter = setter,
                    open = open,
                    explicitlyDeclaredType = explicitlyDeclaredType,
                    lateinit = lateinit
            ))
            else -> raiseConcern("unprocessed MemberNode: ${this}") { listOf<MemberModel>() }
        }
    }

    private fun ParameterNode.processAsLambdaParam(context: TranslationContext = TranslationContext.IRRELEVANT): LambdaParameterModel {
        return LambdaParameterModel(
                type = type.process(context),
                //TODO: we have to do this check because somewhere deep in constrainToDeclaration conversion something is passing empty param
                name = if (name.isEmpty()) {
                    null
                } else {
                    name
                },
                explicitlyDeclaredType = true
        )
    }


    private fun ParameterNode.process(context: TranslationContext = TranslationContext.IRRELEVANT): ParameterModel {
        return ParameterModel(
                type = type.process(context),
                name = name,
                initializer = when {
                    initializer != null -> ExpressionStatementModel(
                            IdentifierExpressionModel(
                                    initializer!!.value
                            ),
                            null
                    )
                    optional -> ExpressionStatementModel(
                            IdentifierExpressionModel(
                                    IdentifierEntity("definedExternally")
                            ),
                            null
                    )
                    else -> null
                },
                vararg = vararg,
                modifier = null
        )
    }

    private fun MetaData?.processMeta(): String? {
        return when (this) {
            is SimpleMetaData -> value
            is IntersectionMetadata -> params.map {
                it.process().translate()
            }.joinToString(" & ")
            else -> null
        }
    }

    private fun ExportQualifier?.toAnnotation(): MutableList<AnnotationModel> {
        return when (this) {
            is JsModule -> mutableListOf(AnnotationModel("JsModule", listOf(name).filterNotNull()))
            is JsDefault -> mutableListOf(AnnotationModel("JsName", listOf(IdentifierEntity("default"))))
            else -> mutableListOf()
        }
    }

    private fun ClassLikeReferenceNode?.convert(): ClassLikeReferenceModel? {
        return this?.let { extendNode ->
            ClassLikeReferenceModel(uidToNameMapper[extendNode.uid]?.fqName
                    ?: extendNode.name, extendNode.typeParameters)
        }
    }

    private fun convertParentEntities(parentEntities: List<HeritageNode>): List<HeritageModel> {
        return parentEntities.map { parentEntity -> parentEntity.convertToModel() }
    }

    private fun VariableNode.resolveGetter(): StatementModel? {
        return if (inline) {
            ExpressionStatementModel(
                    PropertyAccessExpressionModel(
                            PropertyAccessExpressionModel(
                                    ThisExpressionModel(),
                                    CallExpressionModel(
                                            IdentifierExpressionModel(IdentifierEntity("asDynamic")),
                                            listOf()
                                    )
                            ),
                            IdentifierExpressionModel(
                                    name.rightMost()
                            )
                    )
            )
        } else null
    }

    private fun VariableNode.resolveSetter(): StatementModel? {
        return if (inline) {
            AssignmentStatementModel(
                    PropertyAccessExpressionModel(
                            PropertyAccessExpressionModel(
                                    ThisExpressionModel(),
                                    CallExpressionModel(
                                            IdentifierExpressionModel(IdentifierEntity("asDynamic")),
                                            listOf()
                                    )
                            ),
                            IdentifierExpressionModel(
                                    name.rightMost()
                            )
                    ),
                    IdentifierExpressionModel(IdentifierEntity("value"))
            )
        } else null
    }

    private fun ParameterValueDeclaration.isUnit(): Boolean {
        return (this is TypeValueNode) && (value == IdentifierEntity("Unit"))
    }

    private fun BlockStatementModel.wrapBodyAsLazyIterator(): BlockStatementModel {
        return BlockStatementModel(
            statements = listOf(
                ReturnStatementModel(
                    expression = CallExpressionModel(
                        expression = IdentifierExpressionModel(IdentifierEntity("Iterable")),
                        arguments = listOf(
                            LambdaExpressionModel(
                                parameters = listOf(),
                                body = BlockStatementModel(
                                    listOf(
                                        ExpressionStatementModel(
                                            CallExpressionModel(
                                                expression = IdentifierExpressionModel(
                                                    IdentifierEntity("iterator")
                                                ),
                                                arguments = listOf(
                                                    LambdaExpressionModel(
                                                        parameters = listOf(),
                                                        body = this
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    private fun FunctionNode.resolveBody(): BlockStatementModel? {
        val blockStatements = when (val nodeContext = this.context) {
            is IndexSignatureGetter -> listOf(
                    ReturnStatementModel(
                            PropertyAccessExpressionModel(
                                    PropertyAccessExpressionModel(
                                            ThisExpressionModel(),
                                            CallExpressionModel(
                                                    IdentifierExpressionModel(IdentifierEntity("asDynamic")),
                                                    listOf()
                                            )
                                    ),
                                    CallExpressionModel(
                                            IdentifierExpressionModel(
                                                    IdentifierEntity("get")
                                            ),
                                            listOf(
                                                    IdentifierExpressionModel(
                                                            IdentifierEntity(nodeContext.name)
                                                    )
                                            )
                                    )
                            )
                    )
            )

            is IndexSignatureSetter -> listOf(
                    ExpressionStatementModel(
                            PropertyAccessExpressionModel(
                                    PropertyAccessExpressionModel(
                                            ThisExpressionModel(),
                                            CallExpressionModel(
                                                    IdentifierExpressionModel(IdentifierEntity("asDynamic")),
                                                    listOf()
                                            )
                                    ),
                                    CallExpressionModel(
                                            IdentifierExpressionModel(
                                                    IdentifierEntity("set")
                                            ),
                                            listOf(
                                                    IdentifierExpressionModel(
                                                            IdentifierEntity(nodeContext.name)
                                                    ),
                                                    IdentifierExpressionModel(
                                                            IdentifierEntity("value")
                                                    )
                                            )
                                    )
                            )
                    )
            )

            is FunctionFromCallSignature -> {
                val chainCallExpression = PropertyAccessExpressionModel(
                        PropertyAccessExpressionModel(
                                ThisExpressionModel(),
                                CallExpressionModel(
                                        IdentifierExpressionModel(IdentifierEntity("asDynamic")),
                                        listOf()
                                )
                        ),
                        CallExpressionModel(
                                IdentifierExpressionModel(
                                        IdentifierEntity("invoke")
                                ),
                                nodeContext.params.map { IdentifierExpressionModel(it) }
                        )
                )
                listOf(
                        if (type.isUnit()) {
                            ExpressionStatementModel(chainCallExpression)
                        } else {
                            ReturnStatementModel(chainCallExpression)
                        }
                )
            }
            is FunctionFromMethodSignatureDeclaration -> {
                val bodyExpression = PropertyAccessExpressionModel(
                        PropertyAccessExpressionModel(
                                ThisExpressionModel(),
                                CallExpressionModel(
                                        IdentifierExpressionModel(IdentifierEntity("asDynamic")),
                                        listOf()
                                )
                        ),
                        CallExpressionModel(
                                IdentifierExpressionModel(
                                        IdentifierEntity(nodeContext.name)
                                ),
                                nodeContext.params.map { IdentifierExpressionModel(it) }
                        )
                )

                listOf(
                        if (type.isUnit()) {
                            ExpressionStatementModel(bodyExpression)
                        } else {
                            ReturnStatementModel(bodyExpression)
                        }
                )
            }
            else -> null
        }

        return blockStatements?.let { BlockStatementModel(it) }
    }

    private fun convertTypeParams(typeParameters: List<TypeValueNode>, ignoreConstraints: Boolean = false): List<TypeParameterModel> {
        return typeParameters.map { typeParam ->
            TypeParameterModel(
                    type = TypeValueModel(typeParam.value, listOf(), null, typeParam.getFqName()),
                    constraints = if (ignoreConstraints) {
                        emptyList()
                    } else {
                        typeParam.params
                                .map { param -> param.process() }
                                .filterNot { it.isDynamic() }

                    }
            )
        }
    }

    private fun ClassNode.convertToClassModel(): ClassModel {
        val members = processMembers()

        val parentModelEntities = convertParentEntities(parentEntities)

        return ClassModel(
                name = name,
                members = members.ownMembers,
                companionObject = if (members.staticMembers.isNotEmpty()) {
                    ObjectModel(
                            IdentifierEntity(""),
                            members.staticMembers,
                            emptyList(),
                            VisibilityModifierModel.DEFAULT,
                            null,
                            external
                    )
                } else {
                    null
                },
                typeParameters = convertTypeParams(typeParameters),
                parentEntities = parentModelEntities,
                annotations = exportQualifierMap[uid].toAnnotation(),
                comment = null,
                external = external,
                inheritanceModifier = InheritanceModifierModel.OPEN,
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                primaryConstructor = null
        )
    }

    private fun InterfaceNode.convertToInterfaceModel(): InterfaceModel {
        val members = processMembers()

        return InterfaceModel(
                name = name,
                members = members.ownMembers,
                companionObject = if (members.staticMembers.isNotEmpty()) {
                    ObjectModel(
                            IdentifierEntity(""),
                            members.staticMembers,
                            emptyList(),
                            VisibilityModifierModel.DEFAULT,
                            null,
                            false
                    )
                } else {
                    null
                },
                typeParameters = convertTypeParams(typeParameters),
                parentEntities = convertParentEntities(parentEntities),
                annotations = mutableListOf(),
                comment = null,
                external = external,
                visibilityModifier = VisibilityModifierModel.DEFAULT
        )
    }

    private fun MethodNode.process(override: NameEntity? = null): MethodModel {
        return MethodModel(
                name = IdentifierEntity(name),
                parameters = parameters.map { param -> param.process() },
                type = type.process(),
                typeParameters = convertTypeParams(typeParameters),

                static = static,

                override = override,
                operator = operator,
                annotations = resolveAnnotations(),

                open = open,

                body = body?.let {
                    val convertedBody = expressionConverter.convertBlock(it)
                    if (isGenerator) {
                        convertedBody.wrapBodyAsLazyIterator()
                    } else {
                        convertedBody
                    }
                }
        )
    }

    fun TopLevelEntity.convertToModel(moduleOwner: ModuleNode): TopLevelModel? {
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
            is FunctionNode -> {
                FunctionModel(
                        name = name,
                        parameters = parameters.map { param ->
                            val processedParam = param.process()
                            if (inline) {
                                processedParam.copy(initializer = if (param.optional) {
                                    ExpressionStatementModel(
                                            IdentifierExpressionModel(
                                                    IdentifierEntity("null")
                                            ),
                                            null
                                    )
                                } else {
                                    null
                                })
                            } else {
                                processedParam
                            }
                        },
                        type = type.process(),

                        typeParameters = convertTypeParams(typeParameters),
                        annotations = exportQualifierMap[uid].toAnnotation(),
                        export = export,
                        inline = inline,
                        operator = operator,
                        extend = extend.convert(),
                        body = body?.let {
                            val convertedBody = expressionConverter.convertBlock(it)
                            if (isGenerator) {
                                convertedBody.wrapBodyAsLazyIterator()
                            } else {
                                convertedBody
                            }
                        } ?: resolveBody(),
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = null,
                        external = external
                )
            }
            is VariableNode -> {
                val nameResolved = if (moduleOwner.packageName.isStringLiteral()) {
                    (exportQualifierMap[uid] as? JsModule)?.name ?: name
                } else {
                    name
                }
                VariableModel(
                        name = nameResolved,
                        type = type.process(),
                        annotations = exportQualifierMap[uid].toAnnotation(),
                        immutable = exportQualifierMap[uid] is JsModule,
                        inline = inline,
                        external = true,
                        initializer = null,
                        get = resolveGetter(),
                        set = resolveSetter(),
                        typeParameters = convertTypeParams(typeParameters),
                        extend = extend.convert(),
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = null,
                        explicitlyDeclaredType = explicitlyDeclaredType
                )
            }
            is ObjectNode -> ObjectModel(
                    name = name,
                    members = members.flatMap { member -> member.process() },
                    parentEntities = convertParentEntities(parentEntities),
                    visibilityModifier = VisibilityModifierModel.DEFAULT,
                    comment = null,
                    external = external
            )
            is TypeAliasNode -> {
                if (typeReference is UnionTypeDeclaration) {
                    null
                } else {
                    TypeAliasModel(
                            name = name,
                            typeReference = typeReference.process(),
                            typeParameters = convertTypeParams(typeParameters, true),
                            visibilityModifier = VisibilityModifierModel.DEFAULT,
                            comment = null
                    )
                }
            }
            else -> {
                logger.debug("skipping ${this}")
                null
            }
        }
    }

}


private class NodeConverter(
        private val node: SourceSetNode,
        private val uidToNameMapper: UidMapper,
        private val exportQualifierMap: Map<String?, ExportQualifier>
) {

    fun convert(): SourceSetModel {
        return SourceSetModel(
                sourceName = node.sourceName,
                sources = node.sources.flatMap { source ->
                    val rootFile = File(source.fileName)
                    val fileName = rootFile.normalize().absolutePath

                    val generated = mutableListOf<SourceFileModel>()
                    val root = DocumentConverter(source.root, uidToNameMapper, exportQualifierMap).convert(source.fileName, generated)

                    val module = SourceFileModel(
                            name = source.name,
                            fileName = fileName,
                            root = root,
                            referencedFiles = source.referencedFiles.map { referenceFile ->
                                val absolutePath = rootFile.resolveSibling(referenceFile).normalize().absolutePath
                                absolutePath
                            })

                    generated + listOf(module)
                }
        )
    }
}

private class ReferenceVisitor(private val visit: (String, FqNode) -> Unit) {
    fun visitClassLikeNode(declaration: ClassLikeNode, owner: ModuleNode) {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
    }

    fun visitTypeAliasNode(declaration: TypeAliasNode, owner: ModuleNode) {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
    }

    fun visitEnumNode(declaration: EnumNode, owner: ModuleNode) {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
    }

    fun visitModule(node: ModuleNode) {
        node.declarations.forEach { topLevelNode ->
            when (topLevelNode) {
                is ClassLikeNode -> visitClassLikeNode(topLevelNode, node)
                is TypeAliasNode -> visitTypeAliasNode(topLevelNode, node)
                is EnumNode -> visitEnumNode(topLevelNode, node)
                is ModuleNode -> visitModule(topLevelNode)
            }
        }
    }

    fun process(sourceSet: SourceSetNode) {
        sourceSet.sources.forEach { source -> visitModule(source.root) }
    }
}

fun SourceSetDeclaration.introduceModels(moduleNameResolver: ModuleNameResolver): SourceSetModel {
     val exportQualifierMapBuilder = ExportQualifierMapBuilder(moduleNameResolver)
     exportQualifierMapBuilder.lower(this)

    val introduceNodes = IntroduceNodes()
    val nodes = introduceNodes.lower(this)

    val uidToFqNameMapper: MutableMap<String, FqNode> = mutableMapOf()
    ReferenceVisitor { uid, fqModel ->
        uidToFqNameMapper[uid] = fqModel
    }.process(nodes)

    return NodeConverter(nodes, uidToFqNameMapper, exportQualifierMapBuilder.exportQualifierMap).convert()

}