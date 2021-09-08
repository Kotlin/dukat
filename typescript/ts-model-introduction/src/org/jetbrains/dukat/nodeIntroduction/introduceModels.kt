package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MetaData
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.astCommon.SimpleMetaData
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.toNameEntity
import org.jetbrains.dukat.astCommon.ReccurentEntitiesDetector
import org.jetbrains.dukat.astModel.AnnotationModel
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
import org.jetbrains.dukat.astModel.modifiers.InheritanceModifierModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.moduleNameResolver.CommonJsNameResolver
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.stdlib.KLIBROOT
import org.jetbrains.dukat.stdlib.KotlinStdlibEntities
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.JsModule
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceOriginDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WithModifiersDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.NumericLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson
import org.jetbrains.dukat.tsmodel.types.canBeTranslatedAsNumericLiteral
import org.jetbrains.dukat.tsmodel.types.canBeTranslatedAsStringLiteral
import org.jetbrains.dukat.tsmodel.types.makeNullable
import java.io.File

private val logger = Logging.logger("introduceModels")

private fun MemberDeclaration.isStatic() = when (this) {
    is MethodDeclaration -> modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)
    is PropertyDeclaration -> modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)
    else -> false
}

internal sealed class TranslationContext {
    object IRRELEVANT : TranslationContext()
    data class PROPERTY(val optional: Boolean) : TranslationContext()
    object PARAMETER : TranslationContext()
}

private data class Members(
        val ownMembers: List<MemberModel>,
        val staticMembers: List<MemberModel>
)

private typealias UidMapper = Map<String, FqNode>

data class FqNode(val node: TopLevelDeclaration, val fqName: NameEntity)

private fun createKlibType(name: String, nullable: Boolean = false, metaDescription: String? = null): TypeValueModel {
    return TypeValueModel(value = IdentifierEntity(name), params = emptyList(), fqName = KLIBROOT.appendLeft(IdentifierEntity(name)), metaDescription = metaDescription, nullable = nullable)
}

private val UNIT_TYPE = createKlibType("Unit")
private val JSON_TYPE = createKlibType("Json")
private val ANY_TYPE = createKlibType("Any")

private fun dynamicType(metaDescription: String? = null) = TypeValueModel(
        IdentifierEntity("dynamic"),
        emptyList(),
        metaDescription,
        null
)

private fun anyType(nullable: Boolean, metaDescription: String?) = ANY_TYPE.copy(
        nullable = nullable,
        metaDescription = metaDescription
)

// TODO: duplication, think of separate place to have this (but please don't call it utils )))
private fun unquote(name: String): String {
    return name.replace("(?:^[\"|\'`])|(?:[\"|\'`]$)".toRegex(), "")
}

private fun ParameterValueDeclaration.unroll(): List<ParameterValueDeclaration> {
    return when (this) {
        is UnionTypeDeclaration -> params
        else -> listOf(this)
    }
}

private fun Declaration.isOpen() = this !is ObjectLiteralDeclaration

//TODO: this should be done somewhere near escapeIdentificators (at least code should be reused)
private fun escapeName(name: String): String {
    return name
            .replace("/".toRegex(), ".")
            .replace("-".toRegex(), "_")
            .replace("^_$".toRegex(), "`_`")
            .replace("^class$".toRegex(), "`class`")
            .replace("^var$".toRegex(), "`var`")
            .replace("^val$".toRegex(), "`val`")
            .replace("^interface$".toRegex(), "`interface`")
}

private fun TopLevelModel.isUnqualifiable(): Boolean {
    return (this is InterfaceModel) || (this is TypeAliasModel)
}


private fun ModuleDeclaration.shortPackageName(): NameEntity {
    return when (kind) {
        ModuleDeclarationKind.AMBIENT_FILE_PATH -> IdentifierEntity(File(name).nameWithoutExtension)
        ModuleDeclarationKind.AMBIENT_MODULE -> escapeName(unquote(name)).toNameEntity()
        else -> IdentifierEntity(name)
    }
}

private class DocumentConverter(
        private val moduleNode: ModuleDeclaration,
        private val uidToNameMapper: UidMapper,
        private val exportQualifierMap: Map<String?, JsModule>,
        private val rootIsDeclaration: Boolean,
        private val ownerPackageName: NameEntity?
) {
    private val imports = mutableListOf<ImportModel>()
    private val recursionDetector = ReccurentEntitiesDetector<TypeAliasDeclaration>()
    private val expressionConverter = ExpressionConverter { typeNode ->
        typeNode.process()
    }

    @Suppress("UNCHECKED_CAST")
    fun convert(sourceFileName: String, generated: MutableList<SourceFileModel>): ModuleModel {
        val shortName = moduleNode.shortPackageName()
        val fullPackageName = ownerPackageName?.appendLeft(shortName) ?: shortName

        val (roots, topDeclarations) = moduleNode.declarations.partition { it is ModuleDeclaration }

        val topLevelModels = topDeclarations.mapNotNull { declaration ->
            declaration.convertToModel(moduleNode)
        }


        val declarationsMapped = (roots as List<ModuleDeclaration>).map { DocumentConverter(it, uidToNameMapper, exportQualifierMap, rootIsDeclaration, fullPackageName).convert(sourceFileName, generated) } + topLevelModels

        val declarationsFiltered = mutableListOf<TopLevelModel>()
        val submodules = mutableListOf<ModuleModel>()
        declarationsMapped.forEach { declaration ->
            if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
        }

        val annotations = mutableListOf<AnnotationModel>()

        val hasDefaultExport = topDeclarations.any { topDeclaration ->
            val withModifiersDeclaration = topDeclaration as? WithModifiersDeclaration
            (withModifiersDeclaration?.hasExportModifier()  == true) && (withModifiersDeclaration.hasDefaultModifier())
        }

        val moduleNameResolver = CommonJsNameResolver()

        if (topLevelModels.any { !it.isUnqualifiable() }) {
            val jsModuleQualifier = exportQualifierMap[moduleNode.uid]

            if (hasDefaultExport && (jsModuleQualifier?.name == null)) {
                annotations.add(AnnotationModel(IdentifierEntity("JsModule"), listOf(IdentifierEntity(moduleNameResolver.resolveName(moduleNode) ?: moduleNode.name)), AnnotationTarget.FILE))
                annotations.add(AnnotationModel(IdentifierEntity("JsNonModule"), emptyList(), AnnotationTarget.FILE))
            } else {
                jsModuleQualifier?.name?.let { qualifier ->
                    annotations.add(AnnotationModel(IdentifierEntity("JsModule"), listOf(IdentifierEntity(unquote(qualifier))), AnnotationTarget.FILE))
                    annotations.add(AnnotationModel(IdentifierEntity("JsNonModule"), emptyList(), AnnotationTarget.FILE))
                }
            }

            jsModuleQualifier?.qualifier?.let { qualifier ->
                if (qualifier) {
                    annotations.add(AnnotationModel(IdentifierEntity("JsQualifier"), listOf(fullPackageName.process { unquote(it) }), AnnotationTarget.FILE))
                }
            }
        }

        return ModuleModel(
                name = fullPackageName,
                shortName = fullPackageName.rightMost(),
                declarations = declarationsFiltered,
                annotations = annotations,
                submodules = submodules,
                imports = imports,
                comment = null
        )
    }

    private fun TypeAliasDeclaration.isRedundant(typeResolved: TypeModel): Boolean {
        return typeResolved is TypeValueModel &&
                typeResolved.params.isEmpty() && typeParameters.isEmpty() && !typeResolved.nullable &&
                typeResolved.value == aliasName
    }

    private fun ReferenceEntity.getFqName(): NameEntity? {
        return uidToNameMapper[uid]?.fqName
    }

    private fun TypeDeclaration.getFqName(): NameEntity? {
        return typeReference?.getFqName() ?: if (KotlinStdlibEntities.contains(value)) {
            KLIBROOT.appendLeft(value)
        } else null
    }

    private fun ClassLikeDeclaration.processMembers(): Members {
        val (staticNodes, ownNodes) = members.partition { it.isStatic() }
        return Members(ownNodes.flatMap { it.process(this) }, staticNodes.flatMap { it.process(this) })
    }

    private fun UnionTypeDeclaration.convertMeta(): String {
        return params.joinToString(" | ") { unionMember ->
            when (val typeModel = unionMember.process()) {
                is TypeValueModel -> typeModel.metaDescription ?: typeModel.translate()
                else -> typeModel.translate()
            }
        }
    }

    private fun HeritageClauseDeclaration.convertToModel(): HeritageModel {
        val isNamedImport = typeReference?.origin == ReferenceOriginDeclaration.NAMED_IMPORT
        if (isNamedImport) {
            typeReference?.getFqName()?.let { resolvedName ->
                imports.add(ImportModel(resolvedName, name.rightMost()))
            }
        }

        val fqName = if (isNamedImport) {
            name
        } else {
            typeReference?.getFqName()
        }
        return HeritageModel(
                value = TypeValueModel(name, emptyList(), null, fqName),
                typeParams = typeArguments.map { typeArgument -> typeArgument.convertToNode().process() },
                delegateTo = null
        )
    }

    private fun TypeModel.isDynamic(): Boolean {
        return (this is TypeValueModel) && (value == IdentifierEntity("dynamic"))
    }

    private fun ParameterValueDeclaration.isNullable(): Boolean {
        return when (this) {
            is UnionTypeDeclaration -> nullable || params.any { it.isNullable() }
            is TypeParamReferenceDeclaration -> true
            else -> nullable
        }
    }

    private fun TupleDeclaration.process(context: TranslationContext): TypeModel {
        val meta = "JsTuple<${params.map { it.process(context).translate() }.joinToString(", ")}>"
        return when (context) {
            TranslationContext.PARAMETER -> anyType(isNullable(), meta)
            else -> dynamicType(meta)
        }
    }

    fun ParameterValueDeclaration.process(context: TranslationContext = TranslationContext.IRRELEVANT): TypeModel {
        return when (this) {
            is StringLiteralDeclaration -> {
                createKlibType("String", context == TranslationContext.PROPERTY(true), "\"$token\"")
            }
            is NumericLiteralDeclaration -> {
                createKlibType("Number", context == TranslationContext.PROPERTY(true), token)
            }
            is UnionTypeDeclaration -> when {
                canBeTranslatedAsStringLiteral() -> createKlibType("String", context == TranslationContext.PROPERTY(true), convertMeta())
                canBeTranslatedAsNumericLiteral() -> createKlibType("Number", context == TranslationContext.PROPERTY(true), convertMeta())
                else -> when (context) {
                    TranslationContext.PARAMETER -> anyType(isNullable(), convertMeta())
                    else -> dynamicType(convertMeta())
                }

            }
            is TupleDeclaration -> process(context)
            is TypeParamReferenceDeclaration -> {
                TypeParameterReferenceModel(
                        name = value,
                        metaDescription = meta.processMeta(),
                        nullable = nullable
                )
            }
            is TypeDeclaration -> {
                val node = uidToNameMapper[typeReference?.uid]?.node
                val typeAliasNode = node as? TypeAliasDeclaration

                recursionDetector.with(typeAliasNode)  { isAlreadyProcessed ->
                    val dynamic = dynamicType("typealias ${typeAliasNode?.aliasName} = dynamic")

                    if (node is TypeAliasDeclaration && (isAlreadyProcessed || node.typeReference.convertToNode().process().isDynamic())) {
                        dynamic
                    } else {
                        TypeValueModel(
                            value,
                            params.map { it.process(context) }.map { TypeParameterModel(it, listOf()) },
                            meta.processMeta(),
                            getFqName(),
                            nullable
                        )
                            .let { if (recursionDetector.wasRecursionFoundIn(typeAliasNode)) dynamic else it }
                    }
                }
            }
            is FunctionTypeDeclaration -> {
                FunctionTypeModel(
                        parameters = (parameters.map { param ->
                            param.processAsLambdaParam(context)
                        }),
                        type = type.process(context),
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
                                type = typeParam.process(context),
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
                dynamicType()
            }
        }
    }

    private fun convertParameterDeclarations(typeParams: List<TypeParameterDeclaration>): List<TypeDeclaration> {
        return typeParams.map { typeParam ->
            TypeDeclaration(
                    value = typeParam.name,
                    params = typeParam.constraints.map { it.convertToNode() }
            )
        }
    }

    private fun MemberDeclaration.process(owner: Declaration): List<MemberModel> {
        // TODO: how ClassModel end up here?
        return when (this) {
            is ConstructorDeclaration -> listOfNotNull(
                    ConstructorModel(
                            parameters = parameters.map { param -> param.convertToNode().process().copy() },
                            typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters))
                    ),
                    body?.let {
                        InitBlockModel(
                                body = expressionConverter.convertBlock(it)
                        )
                    }
            )
            is CallSignatureDeclaration -> listOf(
                    MethodModel(
                            name = IdentifierEntity("invoke"),
                            parameters = parameters.map { param -> param.convertToNode().process() },
                            type = type.convertToNode().process(),
                            typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),

                            static = false,

                            override = null,
                            operator = true,
                            annotations = listOf(AnnotationModel.NATIVE_INVOKE),

                            open = owner.isOpen(),
                            body = null
                    )
            )
            is IndexSignatureDeclaration -> listOf(
                    MethodModel(
                            name = IdentifierEntity("get"),
                            type = returnType.makeNullable().convertToNode().process(),
                            parameters = parameters.map { param -> param.convertToNode().process() },
                            typeParameters = emptyList(),

                            annotations = listOf(AnnotationModel.NATIVE_GETTER),
                            open = owner.isOpen(),
                            override = null,

                            static = false,
                            operator = true,
                            body = null
                    )
            ) + returnType.convertToNode().unroll().map { unrolledReturnType ->
                MethodModel(
                        name = IdentifierEntity("set"),
                        type = UNIT_TYPE,
                        parameters = parameters.map { param -> param.convertToNode().process() } + ParameterModel(
                                name = "value",
                                type = unrolledReturnType.process(),
                                initializer = null,
                                vararg = false,
                                modifier = null
                        ),
                        typeParameters = emptyList(),

                        annotations = listOf(AnnotationModel.NATIVE_SETTER),
                        open = owner.isOpen(),
                        override = null,

                        static = false,
                        operator = true,
                        body = null
                )
            }
            is MethodSignatureDeclaration -> {
                listOf(
                        MethodModel(
                                name = IdentifierEntity(name),
                                type = type.convertToNode().process(),
                                parameters = parameters.map { param -> param.convertToNode().process() },
                                typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),
                                static = isStatic(),
                                override = null,
                                annotations = emptyList(),
                                open = owner.isOpen(),
                                body = null,
                                operator = false
                        )
                )
            }
            is MethodDeclaration -> listOf(
                    MethodModel(
                            name = IdentifierEntity(name),
                            type = type.convertToNode().process(),
                            parameters = parameters.map { param -> param.convertToNode().process() },
                            typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),
                            static = isStatic(),
                            override = null,
                            annotations = emptyList(),
                            operator = false,
                            open = owner.isOpen(),
                            body = body?.let {
                                val convertedBody = expressionConverter.convertBlock(it)
                                if (isGenerator) {
                                    convertedBody.wrapBodyAsLazyIterator()
                                } else {
                                    convertedBody
                                }
                            }
                    )
            )
            is PropertyDeclaration -> {
                val initializer = expressionConverter.convertExpression(initializer)

                val immutable = modifiers.contains(ModifierDeclaration.SYNTH_IMMUTABLE)

                val typeResolved = if (optional) type.makeNullable() else type

                listOf(PropertyModel(
                        name = IdentifierEntity(name),
                        type = typeResolved.convertToNode().process(TranslationContext.PROPERTY(optional)),
                        typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),
                        static = isStatic(),
                        override = null,
                        immutable = immutable,
                        initializer = initializer,
                        getter = optional,
                        setter = optional && !immutable, // TODO: it's actually wrong
                        open = owner.isOpen(),
                        explicitlyDeclaredType = rootIsDeclaration || explicitlyDeclaredType,
                        lateinit = !rootIsDeclaration && immutable && (initializer == null)
                ))
            }
            else -> raiseConcern("unprocessed MemberDeclaration: ${this}") { listOf<MemberModel>() }
        }
    }

    private fun ParameterDeclaration.processAsLambdaParam(context: TranslationContext = TranslationContext.IRRELEVANT): LambdaParameterModel {
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


    private fun ParameterDeclaration.process(): ParameterModel {
        val initializerResolved = if (initializer != null || optional) {
            TypeDeclaration(IdentifierEntity("definedExternally"), emptyList())
        } else null

        return ParameterModel(
                type = type.process(TranslationContext.PARAMETER),
                name = name,
                initializer = when {
                    initializerResolved != null -> ExpressionStatementModel(
                            IdentifierExpressionModel(
                                    initializerResolved.value
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

    private fun JsModule?.toAnnotation(declaration: WithModifiersDeclaration): MutableList<AnnotationModel> {
        return when {
            this is JsModule -> mutableListOf(AnnotationModel(IdentifierEntity("JsModule"), name?.let { listOf(IdentifierEntity(it)) }
                    ?: emptyList()))
            (declaration.hasDefaultModifier() && declaration.hasExportModifier()) -> mutableListOf(AnnotationModel(IdentifierEntity("JsName"), listOf(IdentifierEntity("default"))))
            else -> mutableListOf()
        }
    }

    private fun convertParentEntities(parentEntities: List<HeritageClauseDeclaration>): List<HeritageModel> {
        return parentEntities.map { parentEntity -> parentEntity.convertToModel() }
    }

    private fun ParameterValueDeclaration.isUnit(): Boolean {
        return (this is TypeDeclaration) && (value == IdentifierEntity("Unit"))
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

    private fun convertTypeParams(typeParameters: List<TypeDeclaration>, ignoreConstraints: Boolean = false): List<TypeParameterModel> {
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

    private fun ClassDeclaration.convertToClassModel(): ClassModel {
        val members = processMembers()

        val parentModelEntities = convertParentEntities(parentEntities)
        val staticParentModelEntities = convertParentEntities(staticallyInherited)

        val external = rootIsDeclaration || hasDeclareModifier()
        return ClassModel(
                name = name,
                members = members.ownMembers,
                companionObject = if (members.staticMembers.isNotEmpty() || staticallyInherited.isNotEmpty()) {
                    ObjectModel(
                            IdentifierEntity(""),
                            members.staticMembers,
                            staticParentModelEntities,
                            VisibilityModifierModel.DEFAULT,
                            null,
                            external
                    )
                } else {
                    null
                },
                typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),
                parentEntities = parentModelEntities,
                annotations = exportQualifierMap[uid].toAnnotation(this),
                comment = null,
                external = external,
                inheritanceModifier = InheritanceModifierModel.OPEN,
                visibilityModifier = VisibilityModifierModel.DEFAULT,
                primaryConstructor = null
        )
    }

    private fun InterfaceDeclaration.convertToInterfaceModel(): InterfaceModel {
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
                typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),
                parentEntities = convertParentEntities(parentEntities),
                annotations = mutableListOf(),
                comment = null,
                external = rootIsDeclaration || hasDeclareModifier(),
                visibilityModifier = VisibilityModifierModel.DEFAULT
        )
    }

    fun TopLevelEntity.convertToModel(moduleOwner: ModuleDeclaration): TopLevelModel? {
        return when (this) {
            is ClassDeclaration -> convertToClassModel()
            is InterfaceDeclaration -> convertToInterfaceModel()
            is GeneratedInterfaceDeclaration -> InterfaceDeclaration(
                    name = name,
                    members = members,
                    typeParameters = typeParameters,
                    parentEntities = parentEntities,
                    uid = uid,
                    modifiers = setOf(ModifierDeclaration.DECLARE_KEYWORD),
                    definitionsInfo = emptyList()
            ).convertToInterfaceModel()
            is EnumDeclaration -> {
                EnumModel(
                        name = IdentifierEntity(name),
                        values = values.map { token -> EnumTokenModel(token.value, token.meta) },
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = null
                )
            }
            is FunctionDeclaration -> {
                FunctionModel(
                        name = IdentifierEntity(name),
                        parameters = parameters.map { param ->
                            param.convertToNode().process()
                        },
                        type = type.convertToNode().process(),

                        typeParameters = convertTypeParams(convertParameterDeclarations(typeParameters)),
                        annotations = exportQualifierMap[uid].toAnnotation(this),
                        export = hasExportModifier(),
                        inline = false,
                        operator = false,
                        extend = null,
                        body = body?.let {
                            val convertedBody = expressionConverter.convertBlock(it)
                            if (isGenerator) {
                                convertedBody.wrapBodyAsLazyIterator()
                            } else {
                                convertedBody
                            }
                        },
                        visibilityModifier = VisibilityModifierModel.DEFAULT,
                        comment = null,
                        external = rootIsDeclaration || hasDeclareModifier()
                )
            }
            is VariableDeclaration -> {
                val variableType = type.convertToNode()

                val nameResolved = if ((moduleOwner.kind == ModuleDeclarationKind.AMBIENT_MODULE) || (moduleOwner.kind == ModuleDeclarationKind.AMBIENT_FILE_PATH)) {
                    exportQualifierMap[uid]?.name?.let { IdentifierEntity(it) } ?: IdentifierEntity(name)
                } else {
                    IdentifierEntity(name)
                }

                val explicitlyDeclaredTypeResolved = rootIsDeclaration || explicitlyDeclaredType
                return if (variableType is ObjectLiteralDeclaration) {
                    if (variableType.canBeJson()) {
                        VariableModel(
                                name = nameResolved,
                                type = JSON_TYPE,
                                annotations = mutableListOf(),
                                immutable = false,
                                inline = false,
                                external = true,
                                initializer = null,
                                get = null,
                                set = null,
                                typeParameters = emptyList(),
                                extend = null,
                                visibilityModifier = VisibilityModifierModel.DEFAULT,
                                comment = null,
                                explicitlyDeclaredType = explicitlyDeclaredTypeResolved
                        )
                    } else {
                        ObjectModel(
                                name = IdentifierEntity(name),
                                members = variableType.members.flatMap { member -> member.process(variableType) },
                                parentEntities = emptyList(),
                                visibilityModifier = VisibilityModifierModel.DEFAULT,
                                comment = null,
                                external = hasDeclareModifier()
                        )
                    }
                } else {
                    VariableModel(
                            name = nameResolved,
                            type = variableType.process(),
                            annotations = exportQualifierMap[uid].toAnnotation(this),
                            immutable = exportQualifierMap[uid] is JsModule,
                            inline = false,
                            external = true,
                            initializer = null,
                            get = null,
                            set = null,
                            typeParameters = emptyList(),
                            extend = null,
                            visibilityModifier = VisibilityModifierModel.DEFAULT,
                            comment = null,
                            explicitlyDeclaredType = explicitlyDeclaredTypeResolved
                    )
                }
            }
            is TypeAliasDeclaration -> {
                val typeReferenceResolved = typeReference.convertToNode().process()
                if (recursionDetector.wasRecursionFoundIn(this) || typeReferenceResolved.isDynamic() || isRedundant(typeReferenceResolved)) {
                    null
                } else {
                    TypeAliasModel(
                            name = aliasName,
                            typeReference = typeReferenceResolved,
                            typeParameters = convertTypeParams(typeParameters.map { typeParameter ->
                                TypeDeclaration(typeParameter.name, typeParameter.constraints.map { it.convertToNode() })
                            }, true),
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
        private val node: SourceSetDeclaration,
        private val uidToNameMapper: UidMapper,
        private val exportQualifierMap: Map<String?, JsModule>
) {

    fun convert(): SourceSetModel {
        return SourceSetModel(
                sourceName = node.sourceName,
                sources = node.sources.flatMap { source ->
                    val rootFile = File(source.fileName)
                    val fileName = rootFile.normalize().absolutePath

                    val generated = mutableListOf<SourceFileModel>()
                    val root = DocumentConverter(source.root, uidToNameMapper, exportQualifierMap, source.root.kind == ModuleDeclarationKind.DECLARATION_FILE, null).convert(source.fileName, generated)

                    val referencedFiles = source.root.imports.map { it.referencedFile } + source.root.references.map { it.referencedFile }

                    val module = SourceFileModel(
                            name = null,
                            fileName = fileName,
                            root = root,
                            referencedFiles = referencedFiles.map { referenceFile ->
                                rootFile.resolveSibling(referenceFile).normalize().absolutePath
                            })

                    generated + listOf(module)
                }
        )
    }
}

private class ReferenceVisitor(private val visit: (String, FqNode) -> Unit) {
    fun visitClassLike(declaration: ClassLikeDeclaration, ownerName: NameEntity) {
        visit(declaration.uid, FqNode(declaration, ownerName.appendLeft(declaration.name)))
    }

    fun visitTypeAlias(declaration: TypeAliasDeclaration, ownerName: NameEntity) {
        visit(declaration.uid, FqNode(declaration, ownerName.appendLeft(declaration.aliasName)))
    }

    fun visitEnum(declaration: EnumDeclaration, ownerName: NameEntity) {
        visit(declaration.uid, FqNode(declaration, ownerName.appendLeft(IdentifierEntity(declaration.name))))
    }

    fun visitModule(declaration: ModuleDeclaration, ownerPackageName: NameEntity?) {
        val shortName = declaration.shortPackageName()
        val qualifiedName = ownerPackageName?.appendLeft(shortName) ?: shortName

        declaration.declarations.forEach { topLevelNode ->
            when (topLevelNode) {
                is ClassLikeDeclaration -> visitClassLike(topLevelNode, qualifiedName)
                is TypeAliasDeclaration -> visitTypeAlias(topLevelNode, qualifiedName)
                is EnumDeclaration -> visitEnum(topLevelNode, qualifiedName)
                is ModuleDeclaration -> visitModule(topLevelNode, qualifiedName)
            }
        }
    }

    fun process(sourceSet: SourceSetDeclaration) {
        sourceSet.sources.forEach { source -> visitModule(source.root, null) }
    }
}

fun SourceSetDeclaration.introduceModels(moduleNameResolver: ModuleNameResolver): SourceSetModel {
    val exportQualifierMapBuilder = ExportQualifierMapBuilder(moduleNameResolver)
    exportQualifierMapBuilder.lower(this)

    val uidToFqNameMapper: MutableMap<String, FqNode> = mutableMapOf()
    ReferenceVisitor { uid, fqModel ->
        uidToFqNameMapper[uid] = fqModel
    }.process(this)

    return NodeConverter(this, uidToFqNameMapper, exportQualifierMapBuilder.exportQualifierMap).convert()

}