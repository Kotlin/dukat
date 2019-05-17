package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.AssignmentStatementNode
import org.jetbrains.dukat.ast.model.nodes.ChainCallNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.EnumTokenNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.GenericIdentifierNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.ReturnStatement
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.StatementCallNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.ast.model.nodes.processing.appendRight
import org.jetbrains.dukat.ast.model.nodes.processing.shiftLeft
import org.jetbrains.dukat.ast.model.nodes.processing.toNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson
import org.jetbrains.dukat.tsmodel.types.isSimpleType
import java.io.File


private class LowerDeclarationsToNodes(private val fileName: String) {

    private fun FunctionDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    private fun PropertyDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    fun convertPropertyDeclaration(declaration: PropertyDeclaration, owner: ClassLikeNode): PropertyNode {
        return PropertyNode(
                declaration.name,
                if (declaration.optional) declaration.type.makeNullable() else declaration.type,
                convertTypeParameters(declaration.typeParameters),

                owner,
                declaration.isStatic(),
                false,
                declaration.optional,
                declaration.optional,  // TODO: it's actually wrong

                true,
                true
        )
    }

    private fun convertParameters(parameters: List<ParameterDeclaration>): List<ParameterNode> {
        return parameters.map { param -> param.convertToNode() }
    }

    private fun convertTypeParameters(typeParams: List<TypeParameterDeclaration>): List<TypeValueNode> {
        return typeParams.map { typeParam ->
            TypeValueNode(
                    value = typeParam.name.toNode(),
                    params = typeParam.constraints
            )
        }
    }

    private fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: ClassLikeNode): MemberNode {
        return if (declaration.optional) {
            PropertyNode(
                    declaration.name,
                    FunctionTypeNode(
                            convertParameters(declaration.parameters),
                            declaration.type,
                            true,
                            null
                    ),
                    convertTypeParameters(declaration.typeParameters),
                    owner,
                    false,
                    false,
                    true,
                    false,
                    true,
                    true
            )
        } else {
            MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type,
                    convertTypeParameters(declaration.typeParameters),
                    owner,
                    false, //TODO: remove static, we don't need it for MethodSignatures
                    false,
                    false,
                    emptyList(),
                    true,
                    true
            )
        }
    }


    private fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration, owner: ClassLikeNode): List<MethodNode> {
        return listOf(
                MethodNode(
                        "get",
                        convertParameters(declaration.indexTypes),
                        declaration.returnType.makeNullable(),
                        emptyList(),
                        owner,
                        false,
                        false,
                        true,
                        listOf(AnnotationNode("nativeGetter", emptyList())),
                        true,
                        true
                ),
                MethodNode(
                        "set",
                        convertParameters(declaration.indexTypes.toMutableList() + listOf(ParameterDeclaration("value", declaration.returnType, null, false, false))),
                        TypeDeclaration(IdentifierEntity("Unit"), emptyList()),
                        emptyList(),
                        owner,
                        false,
                        false,
                        true,
                        listOf(AnnotationNode("nativeSetter", emptyList())),
                        true,
                        true
                )
        )
    }


    private fun CallSignatureDeclaration.convert(owner: ClassLikeNode): MethodNode {
        return MethodNode(
                "invoke",
                convertParameters(parameters),
                type,
                convertTypeParameters(typeParameters),
                owner,
                false,
                false,
                true,
                listOf(AnnotationNode("nativeInvoke", emptyList())),
                true,
                true
        )
    }

    private fun convertToHeritageNodes(declarations: List<HeritageClauseDeclaration>): List<HeritageNode> {
        return declarations.map { declaration ->
            HeritageNode(
                    name = declaration.name.convert(),
                    typeArguments = declaration.typeArguments
            )
        }
    }

    private fun ClassDeclaration.convert(owner: DocumentRootNode?): ClassNode {

        val annotations = mutableListOf<AnnotationNode>()

        if (ModifierDeclaration.hasDefault(modifiers) && ModifierDeclaration.hasExport(modifiers)) {
            annotations.add(AnnotationNode("JsName", listOf(IdentifierEntity("default"))))
        }

        val declaration = ClassNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters.map { typeParameter ->
                    TypeValueNode(typeParameter.name.toNode(), typeParameter.constraints)
                },
                convertToHeritageNodes(parentEntities),
                null,

                owner,
                uid,
                annotations
        )

        declaration.members.forEach { member ->
            when (member) {
                is PropertyNode -> member.copy(owner = declaration)
                is MethodNode -> member.copy(owner = declaration)
                else -> member
            }
        }

        return declaration
    }

    private fun InterfaceDeclaration.convert(): List<TopLevelEntity> {
        val isExternalDefinition = isExternal(definitionsInfo)

        if (isExternalDefinition) {
            return members.flatMap { member -> lowerInlinedInterfaceMemberDeclaration(member, this) }
        }

        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                mutableListOf(),
                null,
                false,
                uid
        )


        declaration.members.forEach { member ->
            when (member) {
                is PropertyNode -> member.copy(owner = declaration)
                is MethodNode -> member.copy(owner = declaration)
                else -> member
            }
        }

        return listOf(declaration)
    }


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                mutableListOf(),
                null,
                true,
                uid
        )


        declaration.members.forEach { member ->
            when (member) {
                is PropertyNode -> member.copy(owner = declaration)
                is MethodNode -> member.copy(owner = declaration)
            }
        }

        return declaration
    }


    private fun ConstructorDeclaration.convert(): ConstructorNode {
        return ConstructorNode(
                convertParameters(parameters),
                convertTypeParameters(typeParameters)
        )
    }

    private fun EnumDeclaration.convert(): EnumNode {
        return EnumNode(
                name = name,
                values = values.map { value -> EnumTokenNode(value.value, value.meta) }
        )
    }

    private fun TypeAliasDeclaration.convert(): TypeAliasNode {
        return TypeAliasNode(
                name = aliasName.toNode(),
                typeReference = typeReference,
                typeParameters = typeParameters.map { typeParameter -> IdentifierEntity(typeParameter.value) },
                canBeTranslated = true
        )
    }

    private fun FunctionDeclaration.convert(): FunctionNode {
        val annotations = mutableListOf<AnnotationNode>()

        val hasExport = ModifierDeclaration.hasExport(modifiers)
        if (ModifierDeclaration.hasDefault(modifiers) && hasExport) {
            annotations.add(AnnotationNode("JsName", listOf(IdentifierEntity("default"))))
        }

        return FunctionNode(
                IdentifierEntity(name),
                convertParameters(parameters),
                type,
                convertTypeParameters(typeParameters),
                mutableListOf(),
                annotations,
                hasExport,
                false,
                false,
                null,
                emptyList(),
                uid
        )
    }

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: ClassLikeNode): MemberNode? {
        val memberDeclaration = convertMethodSignatureDeclaration(declaration, owner)
        return when (memberDeclaration) {
            is PropertyNode -> memberDeclaration
            is MethodNode -> memberDeclaration.copy(
                    parameters = memberDeclaration.parameters,
                    type = memberDeclaration.type
            )
            else -> raiseConcern("unkown method signature") { null }
        }
    }

    private fun mergeTypeParameters(interfaceTypeParams: List<TypeParameterDeclaration>, ownTypeParams: List<TypeParameterDeclaration>): List<TypeParameterDeclaration> {
        val ownNames = ownTypeParams.map { typeParam -> typeParam.name }.toSet()
        return interfaceTypeParams.map { typeParam ->
            val name = typeParam.name
            //TODO: it's quite easy to break this, so consider to introduce "breaking" test and fix for it
            typeParam.copy(name = if (ownNames.contains(name)) {
                when (name) {
                    is IdentifierEntity -> {
                        name.copy(value = name.value + "0")
                    }
                    is QualifierEntity -> {
                        name.copy(right = name.right.copy(value = name.right.value + "0"))
                    }
                    else -> name
                }
            } else {
                name
            })
        }
    }

    private fun lowerInlinedInterfaceMemberDeclaration(declaration: MemberEntity, interfaceDeclaration: InterfaceDeclaration): List<TopLevelEntity> {
        val name = interfaceDeclaration.name

        return when (declaration) {
            is MethodSignatureDeclaration -> {
                val mergeTypeParameters = mergeTypeParameters(interfaceDeclaration.typeParameters, declaration.typeParameters)

                val bodyStatement = ChainCallNode(
                        StatementCallNode(
                                QualifierEntity(
                                        IdentifierEntity("this"),
                                        IdentifierEntity("asDynamic")
                                )
                                , emptyList()),
                        StatementCallNode(IdentifierEntity(declaration.name), declaration.parameters.map { parameter -> IdentifierEntity(parameter.name) })
                )

                listOf(FunctionNode(
                        QualifierEntity(
                                if (interfaceDeclaration.typeParameters.isEmpty()) {
                                    IdentifierEntity(name)
                                } else {
                                    GenericIdentifierNode(name, mergeTypeParameters.map { typeParam ->
                                        TypeValueNode(typeParam.name.toNode(), typeParam.constraints)
                                    })
                                }
                                , IdentifierEntity(declaration.name)),
                        convertParameters(declaration.parameters),
                        declaration.type,
                        convertTypeParameters(mergeTypeParameters + declaration.typeParameters),
                        mutableListOf(),
                        mutableListOf(),
                        true,
                        true,
                        false,
                        null,
                        listOf(
                                if (declaration.type.isSimpleType("Unit")) {
                                    bodyStatement
                                } else {
                                    ReturnStatement(bodyStatement)
                                }
                        ),
                        ""
                ))
            }
            is PropertyDeclaration -> listOf(VariableNode(
                    QualifierEntity(
                            if (interfaceDeclaration.typeParameters.isEmpty()) {
                                IdentifierEntity(name)
                            } else {
                                GenericIdentifierNode(name, interfaceDeclaration.typeParameters.map { typeParam ->
                                    TypeValueNode(typeParam.name.toNode(), typeParam.constraints)
                                })
                            }, IdentifierEntity(declaration.name)
                    ),
                    if (declaration.optional) declaration.type.makeNullable() else declaration.type,
                    mutableListOf(),
                    false,
                    true,
                    null,
                    ChainCallNode(
                            StatementCallNode(
                                    QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()
                            ),
                            StatementCallNode(IdentifierEntity(declaration.name), null)
                    ),
                    AssignmentStatementNode(
                            ChainCallNode(
                                    StatementCallNode(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                                    StatementCallNode(IdentifierEntity(declaration.name), null)
                            ),
                            StatementCallNode(IdentifierEntity("value"), null)
                    ),
                    convertTypeParameters(interfaceDeclaration.typeParameters),
                    null,
                    ""
            ))
            is IndexSignatureDeclaration -> listOf(

                    // TODO: discuss what we actually gonna do when there's more than one key

                    FunctionNode(
                            QualifierEntity(IdentifierEntity(name), IdentifierEntity("get")),
                            convertParameters(declaration.indexTypes),
                            declaration.returnType.makeNullable(),
                            emptyList(),
                            mutableListOf(),
                            mutableListOf(),
                            true,
                            true,
                            true,
                            null,
                            listOf(
                                    ReturnStatement(
                                            ChainCallNode(
                                                    StatementCallNode(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                                                    StatementCallNode(IdentifierEntity("get"), listOf(
                                                            IdentifierEntity(declaration.indexTypes.get(0).name)
                                                    ))
                                            )
                                    )
                            ),
                            ""
                    ),
                    FunctionNode(
                            QualifierEntity(IdentifierEntity(name), IdentifierEntity("set")),
                            convertParameters(declaration.indexTypes + listOf(ParameterDeclaration(
                                    "value", declaration.returnType, null, false, false
                            ))),
                            TypeValueNode(IdentifierEntity("Unit"), emptyList()),
                            emptyList(),
                            mutableListOf(),
                            mutableListOf(),
                            true,
                            true,
                            true,
                            null,
                            listOf(ChainCallNode(
                                    StatementCallNode(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                                    StatementCallNode(IdentifierEntity("set"), listOf(
                                            IdentifierEntity(declaration.indexTypes.get(0).name),
                                            IdentifierEntity("value")
                                    )))
                            ),
                            ""
                    )
            )
            is CallSignatureDeclaration -> listOf(
                    FunctionNode(
                            QualifierEntity(IdentifierEntity(name), IdentifierEntity("invoke")),
                            convertParameters(declaration.parameters.map { param ->
                                val initializer = if (param.initializer?.kind?.isSimpleType("definedExternally") == true) {
                                    ExpressionDeclaration(TypeDeclaration(IdentifierEntity("null"), emptyList()), null)
                                } else {
                                    param.initializer
                                }

                                param.copy(initializer = initializer)
                            }),
                            declaration.type,
                            emptyList(),
                            mutableListOf(),
                            mutableListOf(),
                            true,
                            true,
                            true,
                            null,
                            listOf(ChainCallNode(
                                    StatementCallNode(QualifierEntity(IdentifierEntity("this"), IdentifierEntity("asDynamic")), emptyList()),
                                    StatementCallNode(IdentifierEntity("invoke"), declaration.parameters.map { IdentifierEntity(it.name) }))
                            ),
                            ""
                    )
            )
            else -> emptyList()
        }
    }


    fun lowerMemberDeclaration(declaration: MemberEntity): List<MemberNode> {
        val owner = ROOT_CLASS_DECLARATION
        return when (declaration) {
            is FunctionDeclaration -> listOf(MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type,
                    convertTypeParameters(declaration.typeParameters),
                    owner,
                    declaration.isStatic(),
                    false,
                    false,
                    emptyList(),
                    true,
                    true
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration, owner)).mapNotNull { it }
            is CallSignatureDeclaration -> listOf(declaration.convert(owner))
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration, owner))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration, owner)
            is ConstructorDeclaration -> listOf(declaration.convert())
            else -> raiseConcern("unkown member declaration ${this}") { emptyList<MemberNode>() }
        }
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration): TopLevelEntity {
        val type = declaration.type
        return if (type is ObjectLiteralDeclaration) {

            if (type.canBeJson()) {
                VariableNode(
                        IdentifierEntity(declaration.name),
                        TypeValueNode(IdentifierEntity("Json"), emptyList()),
                        mutableListOf(),
                        false,
                        false,
                        StatementCallNode(IdentifierEntity("definedExternally"), null),
                        null,
                        null,
                        emptyList(),
                        null,
                        declaration.uid
                )
            } else {
                //TODO: don't forget to create owner
                val objectNode = ObjectNode(
                        declaration.name,
                        type.members.flatMap { member -> lowerMemberDeclaration(member) },
                        emptyList()
                )

                objectNode.copy(members = objectNode.members.map {
                    when (it) {
                        is PropertyNode -> it.copy(owner = objectNode, open = false)
                        is MethodNode -> it.copy(owner = objectNode, open = false)
                        else -> it
                    }
                })
            }
        } else {
            VariableNode(
                    IdentifierEntity(declaration.name),
                    type,
                    mutableListOf(),
                    false,
                    false,
                    StatementCallNode(IdentifierEntity("definedExternally"), null),
                    null,
                    null,
                    emptyList(),
                    null,
                    declaration.uid
            )
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelEntity, owner: NodeOwner<PackageDeclaration>): List<TopLevelEntity> {
        return when (declaration) {
            is VariableDeclaration -> listOf(lowerVariableDeclaration(declaration))
            is FunctionDeclaration -> listOf(declaration.convert())
            is ClassDeclaration -> listOf(declaration.convert(null))
            is InterfaceDeclaration -> declaration.convert()
            is GeneratedInterfaceDeclaration -> listOf(declaration.convert())
            is PackageDeclaration -> listOf(lowerPackageDeclaration(declaration, owner.wrap(declaration)))
            is EnumDeclaration -> listOf(declaration.convert())
            is TypeAliasDeclaration -> listOf(declaration.convert())
            else -> listOf(declaration)
        }
    }


    private fun IdentifierEntity.convert(): IdentifierEntity {
        return IdentifierEntity(value)
    }

    private fun NameEntity.convert(): NameEntity {
        return when (this) {
            is IdentifierEntity -> convert()
            is QualifierEntity -> QualifierEntity(
                    left = left.convert(),
                    right = right.convert()
            )
            else -> raiseConcern("unknown QualifiedLeftDeclaration ${this}") { IdentifierEntity("INVALID_ENTITY") }
        }
    }

    private fun unquote(name: String): String {
        return name.replace("(?:^\"|\')|(?:\"|\'$)".toRegex(), "")
    }

    //TODO: this should be done somewhere near escapeIdentificators (at least code should be reused)
    private fun escapePackageName(name: String): String {
        return name
                .replace("/".toRegex(), "")
                .replace("-".toRegex(), "_")
                .replace("^_$".toRegex(), "`_`")
                .replace("^class$".toRegex(), "`class`")
                .replace("^var$".toRegex(), "`var`")
                .replace("^val$".toRegex(), "`val`")
                .replace("^interface$".toRegex(), "`interface`")
    }

    private fun isExternal(definitionsInfo: List<DefinitionInfoDeclaration>): Boolean {
        return definitionsInfo.any { definition ->
            definition.fileName.replace("/", File.separator) != fileName
        }
    }

    fun lowerPackageDeclaration(documentRoot: PackageDeclaration, owner: NodeOwner<PackageDeclaration>): DocumentRootNode {
        val parentDocRoots =
                owner.getOwners().asIterable().reversed().toMutableList() as MutableList<NodeOwner<PackageDeclaration>>

        val rootOwner = parentDocRoots.get(0)

        val qualifiers = parentDocRoots.map { unquote(it.node.packageName) }


        val isQualifier = (documentRoot.packageName == unquote(documentRoot.packageName))

        var showQualifierAnnotation = owner != rootOwner

        val isExternalDefinition = isExternal(documentRoot.definitionsInfo)

        if (isExternalDefinition && isQualifier) {
            showQualifierAnnotation = false
        }

        val qualifierIdentifiers = qualifiers
                .flatMap { it.split("/") }
                .map { IdentifierEntity(escapePackageName(it)) }

        val fullPackageName = qualifierIdentifiers.reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }


        val qualifiedNode = if (qualifiers.isEmpty()) {
            null
        } else {
            qualifiers
                    .map { IdentifierEntity(it) }
                    .reduce<NameEntity, IdentifierEntity> { acc, identifier -> identifier.appendRight(acc) }
                    .shiftLeft()
        }

        val imports = mutableMapOf<String, ImportNode>()
        val nonImports = mutableListOf<TopLevelEntity>()
        documentRoot.declarations.forEach { declaration ->
            if (declaration is ImportEqualsDeclaration) {
                imports[declaration.name] = ImportNode(
                        declaration.moduleReference.convert(),
                        declaration.uid
                )
            } else nonImports.addAll(lowerTopLevelDeclaration(declaration, owner))
        }

        val docRoot = DocumentRootNode(
                fileName,
                documentRoot.resourceName,
                documentRoot.packageName,
                fullPackageName,
                nonImports,
                imports,
                documentRoot.definitionsInfo,
                null,
                documentRoot.uid,
                qualifiedNode,
                isQualifier,
                showQualifierAnnotation
        )


        docRoot.declarations.forEach { declaration ->
            if (declaration is DocumentRootNode) {
                declaration.owner = docRoot
            }
            if (declaration is ClassNode) {
                declaration.owner = docRoot
            }
            if (declaration is InterfaceNode) {
                declaration.owner = docRoot
            }
            if (declaration is FunctionNode) {
                declaration.owner = docRoot
            }
            if (declaration is VariableNode) {
                declaration.owner = docRoot
            }
        }

        return docRoot
    }
}

private fun PackageDeclaration.introduceNodes(fileName: String) = org.jetbrains.dukat.nodeIntroduction.LowerDeclarationsToNodes(fileName).lowerPackageDeclaration(this, NodeOwner(this, null))

fun SourceFileDeclaration.introduceNodes(): SourceFileNode {
    val fileNameNormalized = File(fileName).normalize().absolutePath
    return SourceFileNode(fileNameNormalized, root.introduceNodes(fileNameNormalized), referencedFiles.map { referencedFile -> IdentifierEntity(referencedFile.value) })
}

fun SourceSetDeclaration.introduceNodes() =
        SourceSetNode(sources = sources.map { source ->
            source.introduceNodes()
        })
