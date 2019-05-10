package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.AssignmentStatementNode
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
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.NameNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedNode
import org.jetbrains.dukat.ast.model.nodes.QualifiedStatementNode
import org.jetbrains.dukat.ast.model.nodes.ReturnStatement
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.StatementCallNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.appendRight
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.ast.model.nodes.shiftLeft
import org.jetbrains.dukat.astCommon.AstMemberEntity
import org.jetbrains.dukat.astCommon.AstTopLevelEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedLeftDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
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
                declaration.typeParameters,

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
                    declaration.typeParameters,
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
                    declaration.typeParameters,
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
                        TypeDeclaration(IdentifierDeclaration("Unit"), emptyList()),
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
                typeParameters,
                owner,
                false,
                false,
                true,
                listOf(AnnotationNode("nativeInvoke", emptyList())),
                true,
                true
        )
    }

    private fun HeritageSymbolDeclaration.convert(): HeritageSymbolNode {
        return when (this) {
            is IdentifierDeclaration -> IdentifierNode(value)
            is PropertyAccessDeclaration -> PropertyAccessNode(
                    name = IdentifierNode(name.value),
                    expression = expression.convert()
            )
            else -> throw Exception("unknown heritage symbol ${this}")
        }
    }

    private fun convertToHeritageNodes(declarations: List<HeritageClauseDeclaration>): List<HeritageNode> {
        return declarations.map { declaration ->
            HeritageNode(
                    name = declaration.name.convert(),
                    typeArguments = declaration.typeArguments.map { typeArgument -> IdentifierNode(typeArgument.value) }
            )
        }
    }

    private fun ClassDeclaration.convert(owner: DocumentRootNode?): ClassNode {

        val annotations = mutableListOf<AnnotationNode>()

        if (ModifierDeclaration.hasDefault(modifiers) && ModifierDeclaration.hasExport(modifiers)) {
            annotations.add(AnnotationNode("JsName", listOf(IdentifierNode("default"))))
        }

        val declaration = ClassNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters,
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

    private fun InterfaceDeclaration.convert(): List<AstTopLevelEntity> {
        val isExternalDefinition = isExternal(definitionsInfo)

        if (isExternalDefinition) {
            return members.flatMap { member -> lowerInlinedInterfaceMemberDeclaration(member, this) }
        }

        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters,
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
                typeParameters,
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
                typeParameters
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
                name = aliasName,
                typeReference = typeReference,
                typeParameters = typeParameters.map { typeParameter -> IdentifierNode(typeParameter.value) },
                canBeTranslated = true
        )
    }

    private fun FunctionDeclaration.convert(): FunctionNode {
        val annotations = mutableListOf<AnnotationNode>()

        val hasExport = ModifierDeclaration.hasExport(modifiers)
        if (ModifierDeclaration.hasDefault(modifiers) && hasExport) {
            annotations.add(AnnotationNode("JsName", listOf(IdentifierNode("default"))))
        }

        return FunctionNode(
                IdentifierNode(name),
                convertParameters(parameters),
                type,
                typeParameters,
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

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: ClassLikeNode): MemberNode {
        val memberDeclaration = convertMethodSignatureDeclaration(declaration, owner)
        return when (memberDeclaration) {
            is PropertyNode -> memberDeclaration
            is MethodNode -> memberDeclaration.copy(
                    parameters = memberDeclaration.parameters,
                    type = memberDeclaration.type
            )
            else -> throw Exception("unkown method signature")
        }
    }

    private fun mergeTypeParameters(interfaceTypeParams: List<TypeParameterDeclaration>, ownTypeParams: List<TypeParameterDeclaration>): List<TypeParameterDeclaration> {
        val ownNames = ownTypeParams.map { typeParam -> typeParam.name }.toSet()
        return interfaceTypeParams.map { typeParam ->
            typeParam.copy(name = if (ownNames.contains(typeParam.name)) {
                typeParam.name + "0"
            } else {
                typeParam.name
            })
        }
    }

    private fun lowerInlinedInterfaceMemberDeclaration(declaration: AstMemberEntity, interfaceDeclaration: InterfaceDeclaration): List<AstTopLevelEntity> {
        val name = interfaceDeclaration.name

        return when (declaration) {
            is MethodSignatureDeclaration -> {
                val mergeTypeParameters = mergeTypeParameters(interfaceDeclaration.typeParameters, declaration.typeParameters)

                val bodyStatement = QualifiedStatementNode(
                        QualifiedStatementNode(
                                IdentifierNode("this"),
                                StatementCallNode("asDynamic", emptyList())
                        ),
                        StatementCallNode(declaration.name, declaration.parameters.map { parameter -> IdentifierNode(parameter.name) })
                )

                listOf(FunctionNode(
                        QualifiedNode(
                                if (interfaceDeclaration.typeParameters.isEmpty()) {
                                    IdentifierNode(name)
                                } else {
                                    GenericIdentifierNode(name, mergeTypeParameters)
                                }
                                , IdentifierNode(declaration.name)),
                        convertParameters(declaration.parameters),
                        declaration.type,
                        mergeTypeParameters + declaration.typeParameters,
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
                    QualifiedNode(
                            if (interfaceDeclaration.typeParameters.isEmpty()) {
                                IdentifierNode(name)
                            } else {
                                GenericIdentifierNode(name, interfaceDeclaration.typeParameters)
                            }, IdentifierNode(declaration.name)
                    ),
                    if (declaration.optional) declaration.type.makeNullable() else declaration.type,
                    mutableListOf(),
                    false,
                    true,
                    null,
                    QualifiedStatementNode(
                            QualifiedStatementNode(
                                    IdentifierNode("this"),
                                    StatementCallNode("asDynamic", emptyList())
                            ),
                            IdentifierNode(declaration.name)
                    ),
                    AssignmentStatementNode(
                            QualifiedStatementNode(
                                    QualifiedStatementNode(
                                            IdentifierNode("this"),
                                            StatementCallNode("asDynamic", emptyList())
                                    ),
                                    IdentifierNode(declaration.name)
                            ),
                            IdentifierNode("value")
                    ),
                    interfaceDeclaration.typeParameters,
                    null,
                    ""
            ))
            is IndexSignatureDeclaration -> listOf(

                    // TODO: discuss what we actually gonna do when there's more than one key

                    FunctionNode(
                            QualifiedNode(IdentifierNode(name), IdentifierNode("get")),
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
                                            QualifiedStatementNode(
                                                    QualifiedStatementNode(
                                                            IdentifierNode("this"),
                                                            StatementCallNode("asDynamic", emptyList())
                                                    ),
                                                    StatementCallNode("get", listOf(
                                                            IdentifierNode(declaration.indexTypes.get(0).name)
                                                    ))
                                            )
                                    )
                            ),
                            ""
                    ),
                    FunctionNode(
                            QualifiedNode(IdentifierNode(name), IdentifierNode("set")),
                            convertParameters(declaration.indexTypes + listOf(ParameterDeclaration(
                                    "value", declaration.returnType, null, false, false
                            ))),
                            TypeValueNode("Unit", emptyList()),
                            emptyList(),
                            mutableListOf(),
                            mutableListOf(),
                            true,
                            true,
                            true,
                            null,
                            listOf(QualifiedStatementNode(
                                    QualifiedStatementNode(
                                            IdentifierNode("this"),
                                            StatementCallNode("asDynamic", emptyList())
                                    ),
                                    StatementCallNode("set", listOf(
                                            IdentifierNode(declaration.indexTypes.get(0).name),
                                            IdentifierNode("value")
                                    )))
                            ),
                            ""
                    )
            )
            is CallSignatureDeclaration -> listOf(
                    FunctionNode(
                            QualifiedNode(IdentifierNode(name), IdentifierNode("invoke")),
                            convertParameters(declaration.parameters.map { param ->
                                val initializer = if (param.initializer?.kind?.isSimpleType("definedExternally") == true) {
                                    ExpressionDeclaration(TypeDeclaration(IdentifierDeclaration("null"), emptyList()), null)
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
                            listOf(QualifiedStatementNode(
                                    QualifiedStatementNode(
                                            IdentifierNode("this"),
                                            StatementCallNode("asDynamic", emptyList())
                                    ),
                                    StatementCallNode("invoke", declaration.parameters.map { IdentifierNode(it.name) }))
                            ),
                            ""
                    )
            )
            else -> emptyList()
        }
    }


    fun lowerMemberDeclaration(declaration: AstMemberEntity): List<MemberNode> {
        val owner = ROOT_CLASS_DECLARATION
        return when (declaration) {
            is FunctionDeclaration -> listOf(MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type,
                    declaration.typeParameters,
                    owner,
                    declaration.isStatic(),
                    false,
                    false,
                    emptyList(),
                    true,
                    true
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration, owner))
            is CallSignatureDeclaration -> listOf(declaration.convert(owner))
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration, owner))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration, owner)
            is ConstructorDeclaration -> listOf(declaration.convert())
            else -> throw Exception("unkown member declaration ${this}")
        }
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration): AstTopLevelEntity {
        val type = declaration.type
        return if (type is ObjectLiteralDeclaration) {

            if (type.canBeJson()) {
                VariableNode(
                        IdentifierNode(declaration.name),
                        TypeValueNode("Json", emptyList()),
                        mutableListOf(),
                        false,
                        false,
                        IdentifierNode("definedExternally"),
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
                    IdentifierNode(declaration.name),
                    type,
                    mutableListOf(),
                    false,
                    false,
                    IdentifierNode("definedExternally"),
                    null,
                    null,
                    emptyList(),
                    null,
                    declaration.uid
            )
        }
    }

    fun lowerTopLevelDeclaration(declaration: AstTopLevelEntity, owner: NodeOwner<PackageDeclaration>): List<AstTopLevelEntity> {
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


    private fun IdentifierDeclaration.convert(): IdentifierNode {
        return IdentifierNode(value)
    }

    private fun QualifiedLeftDeclaration.convert(): NameNode {
        return when (this) {
            is IdentifierDeclaration -> convert()
            is QualifiedNamedDeclaration -> QualifiedNode(
                    left = left.convert(),
                    right = right.convert(),
                    nullable = nullable
            )
            else -> throw Exception("unknown QualifiedLeftDeclaration ${this}")
        }
    }

    // TODO: introduce ModuleReferenceNode
    private fun ModuleReferenceDeclaration.convert(): NameNode {
        return when (this) {
            is IdentifierDeclaration -> convert()
            is QualifiedNamedDeclaration -> QualifiedNode(left.convert(), right.convert())
            else -> throw Exception("unknown ModuleReferenceDeclaration ${this}")
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
                .map { IdentifierNode(escapePackageName(it)) }

        val fullPackageName = qualifierIdentifiers.reduce<NameNode, IdentifierNode> { acc, identifier -> identifier.appendRight(acc) }


        val qualifiedNode = if (qualifiers.isEmpty()) {
            null
        } else {
            qualifiers
                    .map { IdentifierNode(it) }
                    .reduce<NameNode, IdentifierNode> { acc, identifier -> identifier.appendRight(acc) }
                    .shiftLeft()
        }

        val imports = mutableMapOf<String, ImportNode>()
        val nonImports = mutableListOf<AstTopLevelEntity>()
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
    return SourceFileNode(fileNameNormalized, root.introduceNodes(fileNameNormalized), referencedFiles.map { referencedFile -> IdentifierNode(referencedFile.value) })
}

fun SourceSetDeclaration.introduceNodes() =
        SourceSetNode(sources = sources.map { source ->
            source.introduceNodes()
        })
