package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionFromCallSignature
import org.jetbrains.dukat.ast.model.nodes.FunctionFromMethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureGetter
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureSetter
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.ReferenceOriginNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.StringLiteralUnionNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.export.JsModule
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.ast.model.nodes.metadata.ThisTypeInGeneratedInterfaceMetaData
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.appendLeft
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
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering
import java.io.File

private val logger = Logging.logger("introduceModels")

private fun MemberNode.isStatic() = when (this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}

internal enum class TranslationContext {
    TYPE_CONSTRAINT,
    IRRELEVANT,
    FUNCTION_TYPE,
    PROPERTY,
    INLINE_EXTENSION,
    CONSTRUCTOR
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


internal class DocumentConverter(private val moduleNode: ModuleNode, private val uidToNameMapper: UidMapper) {
    private val imports = mutableListOf<ImportModel>()

    companion object {
        val IMPOSSIBLE_CONSTRAINT = IdentifierEntity("<IMPOSSIBLE-CONSTRAINT>")
    }

    @Suppress("UNCHECKED_CAST")
    fun convert(sourceFileName: String, generated: MutableList<SourceFileModel>): ModuleModel {
        val (roots, topDeclarations) = moduleNode.declarations.partition { it is ModuleNode }

        val declarationsMapped = (roots as List<ModuleNode>).map { DocumentConverter(it, uidToNameMapper).convert(sourceFileName, generated) } + topDeclarations.mapNotNull { declaration ->
            declaration.convertToModel()
        }

        val declarationsFiltered = mutableListOf<TopLevelModel>()
        val submodules = mutableListOf<ModuleModel>()
        declarationsMapped.forEach { declaration ->
            if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
        }

        val annotations = mutableListOf<AnnotationModel>()

        moduleNode.jsModule?.let {
            annotations.add(AnnotationModel("file:JsModule", listOf(it)))
            annotations.add(AnnotationModel("file:JsNonModule", emptyList()))
        }

        moduleNode.jsQualifier?.let {
            annotations.add(AnnotationModel("file:JsQualifier", listOf(it)))
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

    private fun ReferenceEntity.getFqName(ownerName: NameEntity): NameEntity? {
        return if (uid.startsWith("lib-")) {
            TSLIBROOT.appendLeft(ownerName)
        } else {
            uidToNameMapper[uid]?.fqName
        }
    }

    private fun TypeValueNode.getFqName(): NameEntity? {
        return typeReference?.getFqName(value) ?: if (KotlinStdlibEntities.contains(value)) {
            TSLIBROOT.appendLeft(value)
        } else null
    }

    private fun ClassLikeNode.processMembers(): Members {
        val (staticNodes, ownNodes) = members.partition { it.isStatic() }
        return Members(ownNodes.mapNotNull { it.process() }, staticNodes.mapNotNull { it.process() })
    }

    private fun NameEntity.addLibPrefix() = TSLIBROOT.appendLeft(this)

    private fun UnionTypeNode.convertMeta(): String {
        return params.joinToString(" | ") { unionMember ->
            unionMember.process().translate().let {
                if (nullable) {
                    "${it}?"
                } else {
                    it
                }
            }
        }
    }

    private fun HeritageNode.convertToModel(): HeritageModel {
        val isNamedImport = reference?.origin == ReferenceOriginNode.NAMED_IMPORT
        if (isNamedImport) {
            reference?.getFqName(name)?.let { resolvedName ->
                imports.add(ImportModel(resolvedName, name.rightMost()))
            }
        }

        val fqName = if (isNamedImport) {
            name
        } else {
            reference?.getFqName(name)
        }
        return HeritageModel(
                value = TypeValueModel(name, emptyList(), null, fqName),
                typeParams = typeArguments.map { typeArgument -> typeArgument.process() },
                delegateTo = null
        )
    }

    fun ParameterValueDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): TypeModel {
        val dynamicName = when (context) {
            TranslationContext.TYPE_CONSTRAINT -> IMPOSSIBLE_CONSTRAINT
            else -> IdentifierEntity("dynamic")
        }
        return when (this) {
            is StringLiteralUnionNode -> TypeValueModel(
                    IdentifierEntity("String"),
                    emptyList(),
                    params.joinToString(" | "),
                    null,
                    nullable
            )
            is UnionTypeNode -> TypeValueModel(
                    dynamicName,
                    emptyList(),
                    convertMeta(),
                    null
            )
            is TupleTypeNode -> TypeValueModel(
                    dynamicName,
                    emptyList(),
                    "JsTuple<${params.map { it.process().translate() }.joinToString(", ")}>",
                    null
            )
            is TypeParameterNode -> {
                TypeParameterReferenceModel(
                        name = name,
                        metaDescription = meta.processMeta(),
                        nullable = nullable
                )
            }
            is TypeValueNode -> {
                uidToNameMapper[typeReference?.uid]?.let {
                    if ((it.node is TypeAliasNode) && (it.node.typeReference is UnionTypeNode)) {
                        dynamicType("typealias ${it.node.name.asString()} = dynamic")
                    } else {
                        null
                    }
                } ?: TypeValueModel(
                        value,
                        params.map { param -> param.process() }.map { TypeParameterModel(it, listOf()) },
                        meta.processMeta(),
                        getFqName(),
                        nullable
                )
            }
            is FunctionTypeNode -> {
                FunctionTypeModel(
                        parameters = (parameters.map { param ->
                            param.processAsLambdaParam(TranslationContext.FUNCTION_TYPE)
                        }),
                        type = type.process(TranslationContext.FUNCTION_TYPE),
                        metaDescription = meta.processMeta(),
                        nullable = nullable
                )
            }
            is GeneratedInterfaceReferenceNode -> {
                val typeParams =  when (context) {
                    TranslationContext.PROPERTY -> typeParameters.map {
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
                                type = TypeValueModel(
                                        typeParam.name,
                                        emptyList(),
                                        null,
                                        if (reference?.uid?.endsWith("_GENERATED") == true) {
                                            null
                                        } else {
                                            reference?.getFqName(typeParam.name)
                                        }),
                                constraints = emptyList()
                        )
                    }
                }
                TypeValueModel(
                        name,
                        typeParams,
                        meta?.processMeta(),
                        reference?.getFqName(name),
                        nullable
                )

            }
            else -> raiseConcern("unable to process ParameterValueDeclaration ${this}") {
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

    private fun MemberNode.process(): MemberModel? {
        // TODO: how ClassModel end up here?
        return when (this) {
            is ConstructorNode -> ConstructorModel(
                    parameters = parameters.map { param -> param.process(TranslationContext.CONSTRUCTOR) },
                    typeParameters = convertTypeParams(typeParameters),
                    generated = generated
            )
            is MethodNode -> process()
            is PropertyNode -> PropertyModel(
                    name = IdentifierEntity(name),
                    type = type.process(TranslationContext.PROPERTY),
                    typeParameters = convertTypeParams(typeParameters),
                    static = static,
                    override = null,
                    immutable = getter && !setter,
                    getter = false,
                    setter = false,
                    open = open
            )
            else -> raiseConcern("unprocessed MemberNode: ${this}") { null }
        }
    }

    private fun ParameterNode.processAsLambdaParam(context: TranslationContext = TranslationContext.IRRELEVANT): LambdaParameterModel {
        return LambdaParameterModel(
                type = type.process(context),
                name = name
        )
    }


    private fun ParameterNode.process(context: TranslationContext = TranslationContext.IRRELEVANT): ParameterModel {
        return ParameterModel(
                type = type.process(context),
                name = name,
                initializer = when (context) {
                    TranslationContext.CONSTRUCTOR -> {
                        null
                    }
                    TranslationContext.INLINE_EXTENSION -> {
                        if (optional) {
                            ExpressionStatementModel(
                                    IdentifierExpressionModel(
                                            IdentifierEntity("null")
                                    ),
                                    meta
                            )
                        } else {
                            null
                        }
                    }
                    else -> {
                        when {
                            initializer != null -> ExpressionStatementModel(
                                    IdentifierExpressionModel(
                                            initializer!!.value
                                    ),
                                    meta
                            )
                            optional -> ExpressionStatementModel(
                                    IdentifierExpressionModel(
                                            IdentifierEntity("definedExternally")
                                    ),
                                    meta
                            )
                            else -> null
                        }
                    }
                },
                vararg = vararg,
                modifier = null
        )
    }

    private fun ParameterValueDeclaration?.processMeta(): String? {
        return when (this) {
            is ThisTypeInGeneratedInterfaceMetaData -> "this"
            is IntersectionMetadata -> params.map {
                it.process().translate()
            }.joinToString(" & ")
            else -> null
        }
    }

    private fun ExportQualifier?.toAnnotation(): MutableList<AnnotationModel> {
        return when (this) {
            is JsModule -> mutableListOf(AnnotationModel("JsModule", listOf(name)))
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

    private fun collectParentGeneratedMethods(parentEntities: List<HeritageNode>): List<MethodModel> {
        return parentEntities.flatMap { parentEntity ->
            val node = uidToNameMapper[parentEntity.reference?.uid]?.node
            if (node is InterfaceNode) {
                val generatedMethods = node.members.filterIsInstance(MethodNode::class.java).filter { it.meta?.generated == true }
                generatedMethods.map() {
                    it.process(uidToNameMapper[node.uid]?.fqName)
                }
            } else {
                emptyList()
            }
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

    private fun FunctionNode.resolveBody(): BlockStatementModel {
        return BlockStatementModel(when (val nodeContext = this.context) {
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
            else -> emptyList()
        })
    }

    private fun convertTypeParams(typeParameters: List<TypeValueNode>, ignoreConstraints: Boolean = false): List<TypeParameterModel> {
        return typeParameters.map { typeParam ->
            TypeParameterModel(
                    type = TypeValueModel(typeParam.value, listOf(), null, typeParam.getFqName()),
                    constraints = if (ignoreConstraints) {
                        emptyList()
                    } else {
                        typeParam.params
                                .map { param -> param.process(TranslationContext.TYPE_CONSTRAINT) }
                                .filterNot { (it is TypeValueModel) && (it.value == IMPOSSIBLE_CONSTRAINT) }

                    }
            )
        }
    }

    private fun ClassNode.convertToClassModel(): ClassModel {
        val members = processMembers()

        val parentModelEntities = convertParentEntities(parentEntities)
        val generatedMethods = collectParentGeneratedMethods(parentEntities)

        return ClassModel(
                name = name,
                members = members.ownMembers + generatedMethods,
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
                primaryConstructor = primaryConstructor?.let { constructor ->
                    ConstructorModel(
                            parameters = constructor.parameters.map { param -> param.process() },
                            typeParameters = convertTypeParams(constructor.typeParameters),
                            generated = constructor.generated
                    )
                },
                typeParameters = convertTypeParams(typeParameters),
                parentEntities = parentModelEntities,
                annotations = exportQualifier.toAnnotation(),
                comment = null,
                external = external,
                abstract = false,
                visibilityModifier = VisibilityModifierModel.DEFAULT
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
                annotations = exportQualifier.toAnnotation(),
                comment = null,
                external = true,
                visibilityModifier = VisibilityModifierModel.DEFAULT
        )
    }

    private fun MethodNode.process(override: NameEntity? = null): MethodModel {
        // TODO: how ClassModel end up here?
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
                    ExpressionConverter(this@DocumentConverter).convertBlock(it)
                }
        )
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
            is FunctionNode -> {
                val context = if (inline) {
                    TranslationContext.INLINE_EXTENSION
                } else {
                    TranslationContext.IRRELEVANT
                }
                FunctionModel(
                        name = name,
                        parameters = parameters.map { param -> param.process(context) },
                        type = type.process(),

                        typeParameters = convertTypeParams(typeParameters),
                        annotations = exportQualifier.toAnnotation(),
                        export = export,
                        inline = inline,
                        operator = operator,
                        extend = extend.convert(),
                        body = body?.let {
                            ExpressionConverter(this@DocumentConverter).convertBlock(it)
                        } ?: resolveBody(),
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = comment,
                        external = external
                )
            }
            is VariableNode -> VariableModel(
                    name = name,
                    type = type.process(),
                    annotations = exportQualifier.toAnnotation(),
                    immutable = immutable,
                    inline = inline,
                    external = true,
                    initializer = null,
                    get = resolveGetter(),
                    set = resolveSetter(),
                    typeParameters = convertTypeParams(typeParameters),
                    extend = extend.convert(),
                    visibilityModifier = VisibilityModifierModel.DEFAULT,
                    comment = null
            )
            is ObjectNode -> ObjectModel(
                    name = name,
                    members = members.mapNotNull { member -> member.process() },
                    parentEntities = convertParentEntities(parentEntities),
                    visibilityModifier = VisibilityModifierModel.DEFAULT,
                    comment = null,
                    external = external
            )
            is TypeAliasNode -> {
                if (typeReference is UnionTypeNode) {
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


private class NodeConverter(private val node: SourceSetNode, private val uidToNameMapper: UidMapper) {

    fun convert(): SourceSetModel {
        return SourceSetModel(
                sourceName = node.sourceName,
                sources = node.sources.flatMap { source ->
                    val rootFile = File(source.fileName)
                    val fileName = rootFile.normalize().absolutePath

                    val generated = mutableListOf<SourceFileModel>()
                    val root = DocumentConverter(source.root, uidToNameMapper).convert(source.fileName, generated)

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

private class ReferenceVisitor(private val visit: (String, FqNode) -> Unit) : NodeTypeLowering {
    override fun lowerClassLikeNode(declaration: ClassLikeNode, owner: ModuleNode): ClassLikeNode {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
        return super.lowerClassLikeNode(declaration, owner)
    }

    override fun lowerTypeAliasNode(declaration: TypeAliasNode, owner: ModuleNode): TypeAliasNode {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
        return super.lowerTypeAliasNode(declaration, owner)
    }

    override fun lowerEnumNode(declaration: EnumNode, owner: ModuleNode): EnumNode {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
        return super.lowerEnumNode(declaration, owner)
    }

    fun process(sourceSet: SourceSetNode) {
        sourceSet.sources.forEach { source -> lowerModuleNode(source.root) }
    }
}

fun SourceSetNode.introduceModels(): SourceSetModel {
    val uidToFqNameMapper: MutableMap<String, FqNode> = mutableMapOf()

    ReferenceVisitor { uid, fqModel ->
        uidToFqNameMapper[uid] = fqModel
    }.process(this)

    return NodeConverter(this, uidToFqNameMapper).convert()
}