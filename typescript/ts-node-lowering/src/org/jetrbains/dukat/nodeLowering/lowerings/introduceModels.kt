package org.jetrbains.dukat.nodeLowering.lowerings

import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
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
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.ReferenceOriginNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
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
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetrbains.dukat.nodeLowering.NodeTypeLowering
import java.io.File

private val logger = Logging.logger("introduceModels")

private fun MemberNode.isStatic() = when (this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}

private enum class TranslationContext {
    TYPE_CONSTRAINT,
    IRRELEVANT,
    FUNCTION_TYPE,
    INLINE_EXTENSION,
    CONSTRUCTOR
}

private data class Members(
        val dynamic: List<MemberModel>,
        val static: List<MemberModel>
)

private typealias UidMapper = Map<String, FqNode>
private typealias UidMutableMapper = MutableMap<String, FqNode>

data class FqNode(val node: Entity, val fqName: NameEntity)

private class DocumentConverter(private val documentRootNode: DocumentRootNode, private val uidToNameMapper: UidMapper) {
    private val imports = mutableListOf<ImportModel>()

    companion object {
        val IMPOSSIBLE_CONSTRAINT = IdentifierEntity("<IMPOSSIBLE-CONSTRAINT>")
    }

    @Suppress("UNCHECKED_CAST")
    fun convert(sourceFileName: String, generated: MutableList<SourceFileModel>): ModuleModel {
        val (roots, topDeclarations) = documentRootNode.declarations.partition { it is DocumentRootNode }

        val declarationsMapped = (roots as List<DocumentRootNode>).map { DocumentConverter(it, uidToNameMapper).convert(sourceFileName, generated) } + topDeclarations.mapNotNull { declaration ->
            declaration.convertToModel()
        }

        val declarationsFiltered = mutableListOf<TopLevelModel>()
        val submodules = mutableListOf<ModuleModel>()
        declarationsMapped.forEach { declaration ->
            if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
        }

        val annotations = mutableListOf<AnnotationModel>()

        documentRootNode.jsModule?.let {
            annotations.add(AnnotationModel("file:JsModule", listOf(it)))
            annotations.add(AnnotationModel("file:JsNonModule", emptyList()))
        }

        documentRootNode.jsQualifier?.let {
            annotations.add(AnnotationModel("file:JsQualifier", listOf(it)))
        }

        return ModuleModel(
                name = documentRootNode.qualifiedPackageName,
                shortName = documentRootNode.qualifiedPackageName.rightMost(),
                declarations = declarationsFiltered,
                annotations = annotations,
                submodules = submodules,
                imports = imports,
                comment = null
        )
    }

    private fun ReferenceEntity.getFqName(ownerName: NameEntity): NameEntity? {
        return if (uid.startsWith("lib-")) {
            IdentifierEntity("<LIBROOT>").appendLeft(ownerName)
        } else {
            uidToNameMapper[uid]?.fqName
        }
    }

    private fun TypeValueNode.getFqName(): NameEntity? {
        return typeReference?.getFqName(value) ?: if (KotlinStdlibEntities.contains(value)) {
            IdentifierEntity("<LIBROOT>").appendLeft(value)
        } else null
    }

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

    private fun NameEntity.addLibPrefix() = IdentifierEntity("<LIBROOT>").appendLeft(this)
    private fun UnionTypeNode.canBeTranslatedAsString(): Boolean {
        return params.all { (it is TypeValueNode) && (it.value == IdentifierEntity("String")) }
    }

    private fun UnionTypeNode.convertMeta(): String {
        return params.joinToString(" | ") { unionMember ->
            if (unionMember.meta is StringLiteralDeclaration) {
                (unionMember.meta as StringLiteralDeclaration).token
            } else {
                unionMember.process().translate()
            }.let {
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

    private fun ParameterValueDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): TypeModel {
        val dynamicName = when (context) {
            TranslationContext.TYPE_CONSTRAINT -> IMPOSSIBLE_CONSTRAINT
            else -> IdentifierEntity("dynamic")
        }
        return when (this) {
            is UnionTypeNode -> if (canBeTranslatedAsString()) {
                val stringEntity = IdentifierEntity("String")
                TypeValueModel(
                        stringEntity,
                        emptyList(),
                        convertMeta(),
                        stringEntity.addLibPrefix()
                )
            } else {
                TypeValueModel(
                        dynamicName,
                        emptyList(),
                        convertMeta(),
                        null
                )
            }
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
                if ((value == IdentifierEntity("String")) && (meta is StringLiteralDeclaration)) {
                    TypeValueModel(value, emptyList(), (meta as StringLiteralDeclaration).token, typeReference?.getFqName(value))
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
                            param.process(TranslationContext.FUNCTION_TYPE)
                        }),
                        type = type.process(TranslationContext.FUNCTION_TYPE),
                        metaDescription = meta.processMeta(),
                        nullable = nullable
                )
            }
            is GeneratedInterfaceReferenceNode -> {
                TypeValueModel(
                        name,
                        typeParameters.map { typeParam ->
                            TypeValueModel(
                                    typeParam.name,
                                    emptyList(),
                                    null,
                                    if (reference?.uid?.endsWith("_GENERATED") == true) {
                                        null
                                    } else {
                                        reference?.getFqName(typeParam.name)
                                    })
                        }.map { TypeParameterModel(it, listOf()) },
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
                    type = type.process(TranslationContext.IRRELEVANT),
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

    private fun convertParentEntities(parentEntities: List<HeritageNode>, generatedMethodsHandler: ((methods: List<MethodModel>) -> Unit)? = null): List<HeritageModel> {
        return parentEntities.map { parentEntity ->
            val node = uidToNameMapper[parentEntity.reference?.uid]?.node
            if (node is InterfaceNode) {
                val generatedMethods = node.members.filterIsInstance(MethodNode::class.java).filter { it.meta?.generated == true }
                if (!generatedMethods.isEmpty()) {
                    generatedMethodsHandler?.invoke(generatedMethods.map { it.copy() }.map { it.process(uidToNameMapper[node.uid]?.fqName) })
                }
            }
            parentEntity.convertToModel()
        }
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

    private fun FunctionNode.resolveBody(): List<StatementModel> {
        return when (val nodeContext = this.context) {
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
        }
    }

    private fun convertTypeParams(typeParameters: List<TypeValueNode>): List<TypeParameterModel> {
        return typeParameters.map { typeParam ->
            TypeParameterModel(
                    type = TypeValueModel(typeParam.value, listOf(), null, typeParam.getFqName()),
                    constraints = typeParam.params
                            .map { param -> param.process(TranslationContext.TYPE_CONSTRAINT) }
                            .filterNot { (it is TypeValueModel) && (it.value == IMPOSSIBLE_CONSTRAINT) }
            )
        }
    }

    private fun ClassNode.convertToClassModel(): TopLevelModel {
        val membersSplitted = split(members)

        val generatedMethods = mutableListOf<MemberModel>()
        val parentModelEntities = convertParentEntities(parentEntities) {
            generatedMethods.addAll(it)
        }
        return ClassModel(
                name = name,
                members = membersSplitted.dynamic + generatedMethods,
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
                external = true,
                abstract = false,
                visibilityModifier = VisibilityModifierModel.DEFAULT
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

                body = body?.convert()
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
                        body = body?.convert() ?: resolveBody(),
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = comment
                )
            }
            is VariableNode -> VariableModel(
                    name = name,
                    type = type.process(),
                    annotations = exportQualifier.toAnnotation(),
                    immutable = immutable,
                    inline = inline,
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
                    comment = null
            )
            is TypeAliasNode -> {
                TypeAliasModel(
                        name = name,
                        typeReference = typeReference.process(),
                        typeParameters = typeParameters.map { typeParameter -> TypeParameterModel(TypeValueModel(typeParameter, listOf(), null, null), emptyList()) },
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = null
                )
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
    override fun lowerClassLikeNode(declaration: ClassLikeNode, owner: DocumentRootNode): ClassLikeNode {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
        return super.lowerClassLikeNode(declaration, owner)
    }

    override fun lowerTypeAliasNode(declaration: TypeAliasNode, owner: DocumentRootNode): TypeAliasNode {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
        return super.lowerTypeAliasNode(declaration, owner)
    }

    override fun lowerEnumNode(declaration: EnumNode, owner: DocumentRootNode): EnumNode {
        visit(declaration.uid, FqNode(declaration, owner.qualifiedPackageName.appendLeft(declaration.name)))
        return super.lowerEnumNode(declaration, owner)
    }

    fun process(sourceSet: SourceSetNode) {
        sourceSet.sources.forEach { source -> lowerDocumentRoot(source.root) }
    }
}

fun SourceSetNode.introduceModels(uidToFqNameMapper: UidMutableMapper): SourceSetModel {
    ReferenceVisitor { uid, fqModel ->
        uidToFqNameMapper[uid] = fqModel
    }.process(this)

    return NodeConverter(this, uidToFqNameMapper).convert()
}