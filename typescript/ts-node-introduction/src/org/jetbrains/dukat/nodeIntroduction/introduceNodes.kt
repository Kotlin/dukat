package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ClassLikeReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.EnumTokenNode
import org.jetbrains.dukat.ast.model.nodes.FunctionFromCallSignature
import org.jetbrains.dukat.ast.model.nodes.FunctionFromMethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNodeContextIrrelevant
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureGetter
import org.jetbrains.dukat.ast.model.nodes.IndexSignatureSetter
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.convertToNode
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.SimpleCommentEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.appendRight
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.unquote
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
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
import java.io.File
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode as DocumentRootNode1


private fun unquote(name: String): String {
    return name.replace("(?:^[\"|\'`])|(?:[\"|\'`]$)".toRegex(), "")
}


private class LowerDeclarationsToNodes(private val fileName: String, private val moduleNameResolver: ModuleNameResolver) {

    private fun FunctionDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    private fun PropertyDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    fun convertPropertyDeclaration(declaration: PropertyDeclaration): PropertyNode {
        return PropertyNode(
                declaration.name,
                if (declaration.optional) declaration.type.makeNullable() else declaration.type,
                convertTypeParameters(declaration.typeParameters),

                declaration.isStatic(),
                declaration.optional,
                declaration.optional,  // TODO: it's actually wrong

                true
        )
    }

    private fun convertParameters(parameters: List<ParameterDeclaration>): List<ParameterNode> {
        return parameters.map { param -> param.convertToNode() }
    }

    private fun convertTypeParameters(typeParams: List<TypeParameterDeclaration>): List<TypeValueNode> {
        return typeParams.map { typeParam ->
            TypeValueNode(
                    value = typeParam.name,
                    params = typeParam.constraints
            )
        }
    }

    private fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberNode {
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
                    false,
                    true,
                    false,
                    true
            )
        } else {
            MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type,
                    convertTypeParameters(declaration.typeParameters),
                    false,
                    false,
                    true,
                    null
            )
        }
    }


    private fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): List<MethodNode> {
        return listOf(
                MethodNode(
                        "get",
                        convertParameters(declaration.indexTypes),
                        declaration.returnType.makeNullable(),
                        emptyList(),
                        false,
                        true,
                        true,
                        null
                ),
                MethodNode(
                        "set",
                        convertParameters(declaration.indexTypes.toMutableList() + listOf(ParameterDeclaration("value", declaration.returnType, null, false, false))),
                        TypeDeclaration(IdentifierEntity("Unit"), emptyList()),
                        emptyList(),
                        false,
                        true,
                        true,
                        null
                )
        )
    }


    private fun CallSignatureDeclaration.convert(): MethodNode {
        return MethodNode(
                "invoke",
                convertParameters(parameters),
                type,
                convertTypeParameters(typeParameters),
                false,
                true,
                true,
                null
        )
    }

    private fun convertToHeritageNodes(declarations: List<HeritageClauseDeclaration>): List<HeritageNode> {
        return declarations.map { declaration ->
            HeritageNode(
                    name = declaration.name.convert(),
                    typeArguments = declaration.typeArguments,
                    reference = declaration.typeReference
            )
        }
    }

    private fun ClassDeclaration.convert(): ClassNode {

        val exportQualifier = if (ModifierDeclaration.hasDefault(modifiers) && ModifierDeclaration.hasExport(modifiers)) {
            JsDefault()
        } else null

        val declaration = ClassNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters.map { typeParameter ->
                    TypeValueNode(typeParameter.name, typeParameter.constraints)
                },
                convertToHeritageNodes(parentEntities),
                null,

                uid,
                exportQualifier
        )

        return declaration
    }

    private fun InterfaceDeclaration.convert(): List<TopLevelEntity> {
        resolveExternalSource(definitionsInfo)?.let { originalSource ->
            return members.flatMap { member -> lowerInlinedInterfaceMemberDeclaration(member, this, originalSource) }
        }

        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                null,
                false,
                uid
        )

        return listOf(declaration)
    }


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                null,
                true,
                uid
        )

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
                name = IdentifierEntity(name),
                values = values.map { value -> EnumTokenNode(value.value, value.meta) },
                uid = uid
        )
    }

    private fun TypeAliasDeclaration.convert(): TypeAliasNode {
        return TypeAliasNode(
                name = aliasName,
                typeReference = typeReference,
                typeParameters = typeParameters,
                uid = uid
        )
    }

    private fun FunctionDeclaration.convert(): FunctionNode {
        val hasExport = ModifierDeclaration.hasExport(modifiers)

        val exportQualifier = if (ModifierDeclaration.hasDefault(modifiers) && hasExport) {
            JsDefault()
        } else null

        return FunctionNode(
                IdentifierEntity(name),
                convertParameters(parameters),
                type,
                convertTypeParameters(typeParameters),
                mutableListOf(),
                exportQualifier,
                hasExport,
                false,
                false,
                null,
                FunctionNodeContextIrrelevant(),
                uid,
                null
        )
    }

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberNode? {
        val memberDeclaration = convertMethodSignatureDeclaration(declaration)
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
                }
            } else {
                name
            })
        }
    }

    private fun lowerInlinedInterfaceMemberDeclaration(declaration: MemberEntity, interfaceDeclaration: InterfaceDeclaration, originalSource: String): List<TopLevelEntity> {
        val name = interfaceDeclaration.name
        val inlineSourceComment = SimpleCommentEntity("extending interface from $originalSource")

        return when (declaration) {
            is MethodSignatureDeclaration -> {
                val mergeTypeParameters = mergeTypeParameters(interfaceDeclaration.typeParameters, declaration.typeParameters)

                listOf(FunctionNode(
                        IdentifierEntity(declaration.name),
                        convertParameters(declaration.parameters),
                        declaration.type,
                        convertTypeParameters(mergeTypeParameters + declaration.typeParameters),
                        mutableListOf(),
                        null,
                        true,
                        true,
                        false,
                        ClassLikeReferenceNode(interfaceDeclaration.uid, name, mergeTypeParameters.map { typeParam ->
                            typeParam.name
                        }),
                        FunctionFromMethodSignatureDeclaration(declaration.name, declaration.parameters.map { IdentifierEntity(it.name) }),
                        "",
                        inlineSourceComment
                ))
            }
            is PropertyDeclaration -> listOf(VariableNode(
                    IdentifierEntity(declaration.name),
                    if (declaration.optional) declaration.type.makeNullable() else declaration.type,
                    null,
                    false,
                    true,
                    convertTypeParameters(interfaceDeclaration.typeParameters),
                    ClassLikeReferenceNode(interfaceDeclaration.uid, name, interfaceDeclaration.typeParameters.map { typeParam ->
                        typeParam.name
                    }),
                    "",
                    inlineSourceComment
            ))
            is IndexSignatureDeclaration -> listOf(

                    // TODO: discuss what we actually gonna do when there's more than one key

                    FunctionNode(
                            QualifierEntity(name, IdentifierEntity("get")),
                            convertParameters(declaration.indexTypes),
                            declaration.returnType.makeNullable(),
                            emptyList(),
                            mutableListOf(),
                            null,
                            true,
                            true,
                            true,
                            null,
                            IndexSignatureGetter(declaration.indexTypes[0].name),
                            "",
                            null
                    ),
                    FunctionNode(
                            QualifierEntity(name, IdentifierEntity("set")),
                            convertParameters(declaration.indexTypes + listOf(ParameterDeclaration(
                                    "value", declaration.returnType, null, false, false
                            ))),
                            TypeValueNode(IdentifierEntity("Unit"), emptyList()),
                            emptyList(),
                            mutableListOf(),
                            null,
                            true,
                            true,
                            true,
                            null,
                            IndexSignatureSetter(declaration.indexTypes[0].name),
                            "",
                            null
                    )
            )
            is CallSignatureDeclaration -> listOf(
                    FunctionNode(
                            QualifierEntity(name, IdentifierEntity("invoke")),
                            convertParameters(declaration.parameters),
                            declaration.type,
                            emptyList(),
                            mutableListOf(),
                            null,
                            true,
                            true,
                            true,
                            null,
                            FunctionFromCallSignature(declaration.parameters.map { IdentifierEntity(it.name) }),
                            "",
                            null
                    )
            )
            else -> emptyList()
        }
    }


    fun lowerMemberDeclaration(declaration: MemberEntity): List<MemberNode> {
        return when (declaration) {
            is FunctionDeclaration -> listOf(MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type,
                    convertTypeParameters(declaration.typeParameters),
                    declaration.isStatic(),
                    false,
                    true,
                    null
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration)).mapNotNull { it }
            is CallSignatureDeclaration -> listOf(declaration.convert())
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration)
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
                        null,
                        false,
                        false,
                        emptyList(),
                        null,
                        declaration.uid,
                        null
                )
            } else {
                //TODO: don't forget to create owner
                val objectNode = ObjectNode(
                        IdentifierEntity(declaration.name),
                        type.members.flatMap { member -> lowerMemberDeclaration(member) },
                        emptyList(),
                        declaration.uid
                )

                objectNode.copy(members = objectNode.members.map {
                    when (it) {
                        is PropertyNode -> it.copy(open = false)
                        is MethodNode -> it.copy(open = false)
                        else -> it
                    }
                })
            }
        } else {
            VariableNode(
                    IdentifierEntity(declaration.name),
                    type,
                    null,
                    false,
                    false,
                    emptyList(),
                    null,
                    declaration.uid,
                    null
            )
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelEntity, owner: NodeOwner<ModuleDeclaration>): List<TopLevelEntity> {
        return when (declaration) {
            is VariableDeclaration -> listOf(lowerVariableDeclaration(declaration))
            is FunctionDeclaration -> listOf(declaration.convert())
            is ClassDeclaration -> listOf(declaration.convert())
            is InterfaceDeclaration -> declaration.convert()
            is GeneratedInterfaceDeclaration -> listOf(declaration.convert())
            is ModuleDeclaration -> listOf(lowerPackageDeclaration(declaration, owner.wrap(declaration)))
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
        }
    }

    private fun resolveExternalSource(definitionsInfo: List<DefinitionInfoDeclaration>): String? {
        return definitionsInfo.firstOrNull()?.fileName?.replace("/", File.separator)?.let {
            if (it != fileName) {
                it.split(File.separator).last()
            } else null
        }
    }

    private fun NameEntity.isStringLiteral(): Boolean {
        return when (this) {
            is IdentifierEntity -> value.matches("^[\"\'].*[\"\']$".toRegex())
            else -> false
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerPackageDeclaration(documentRoot: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>): DocumentRootNode1 {
        val parentDocRoots =
                owner.getOwners().asIterable().reversed().toMutableList() as MutableList<NodeOwner<ModuleDeclaration>>

        val qualifiers = parentDocRoots.map { it.node.packageName.unquote() }
        val fullPackageName = qualifiers.reduce { acc, identifier -> identifier.appendRight(acc) }

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

        val moduleNameIsStringLiteral = documentRoot.packageName.isStringLiteral()

        val moduleName = if (moduleNameIsStringLiteral) {
            documentRoot.packageName.process { unquote(it) }
        } else {
            moduleNameResolver.resolveName(fileName)?.let { IdentifierEntity(it) }
        }

        return DocumentRootNode1(
                moduleName = moduleName,
                packageName = documentRoot.packageName,
                qualifiedPackageName = fullPackageName,
                declarations = nonImports,
                imports = imports,
                definitionsInfo = documentRoot.definitionsInfo,
                external = moduleNameIsStringLiteral,
                jsModule = null,
                jsQualifier = null,
                uid = documentRoot.uid
        )
    }
}

private fun ModuleDeclaration.introduceNodes(fileName: String, moduleNameResolver: ModuleNameResolver) = LowerDeclarationsToNodes(fileName, moduleNameResolver).lowerPackageDeclaration(this, NodeOwner(this, null))

fun SourceFileDeclaration.introduceNodes(moduleNameResolver: ModuleNameResolver): SourceFileNode {
    val fileNameNormalized = File(fileName).normalize().absolutePath
    return SourceFileNode(
            fileNameNormalized,
            root.introduceNodes(fileNameNormalized, moduleNameResolver),
            referencedFiles.map { referencedFile -> referencedFile },
            null
    )
}

fun SourceSetDeclaration.introduceNodes(moduleNameResolver: ModuleNameResolver) =
        SourceSetNode(sourceName = sourceName, sources = sources.map { source ->
            source.introduceNodes(moduleNameResolver)
        })
