package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNodeContextIrrelevant
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson
import org.jetbrains.dukat.tsmodel.types.makeNullable


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

private fun String.unquote(): String {
    return replace("(?:^[\"\'])|(?:[\"\']$)".toRegex(), "")
}

private fun NameEntity.unquote(): NameEntity {
    return when (this) {
        is IdentifierEntity -> copy(value = escapeName(value.unquote()))
        else -> this
    }
}


private class LowerDeclarationsToNodes {

    private fun FunctionDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    private fun PropertyDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    fun convertPropertyDeclaration(declaration: PropertyDeclaration, inDeclaredDeclaration: Boolean): PropertyNode {
        val parameterValueDeclaration = if (declaration.optional) declaration.type.makeNullable() else declaration.type
        return PropertyNode(
                name = declaration.name,
                type = parameterValueDeclaration.convertToNode(),
                typeParameters = convertTypeParameters(declaration.typeParameters),

                static = declaration.isStatic(),
                initializer = declaration.initializer,
                getter = declaration.optional,
                setter = declaration.optional,  // TODO: it's actually wrong

                open = true,

                explicitlyDeclaredType = inDeclaredDeclaration || declaration.explicitlyDeclaredType,

                lateinit = !inDeclaredDeclaration && (declaration.initializer == null)
        )
    }

    private fun convertParameters(parameters: List<ParameterDeclaration>): List<ParameterDeclaration> {
        return parameters.map { param -> param.convertToNode() }
    }

    private fun convertTypeParameters(typeParams: List<TypeParameterDeclaration>): List<TypeDeclaration> {
        return typeParams.map { typeParam ->
            TypeDeclaration(
                    value = typeParam.name,
                    params = typeParam.constraints.map { it.convertToNode() }
            )
        }
    }

    private fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberDeclaration {
        return if (declaration.optional) {
            PropertyNode(
                    name = declaration.name,
                    type = FunctionTypeDeclaration(
                            convertParameters(declaration.parameters),
                            declaration.type.convertToNode(),
                            true,
                            null
                    ),
                    typeParameters = convertTypeParameters(declaration.typeParameters),
                    static = false,
                    initializer = null,
                    getter = true,
                    setter = false,
                    open = true,
                    explicitlyDeclaredType = true,
                    lateinit = false
            )
        } else {
            MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type.convertToNode(),
                    convertTypeParameters(declaration.typeParameters),
                    false,
                    false,
                    true,
                    null,
                    false
            )
        }
    }

    private fun ParameterValueDeclaration.unroll(): List<ParameterValueDeclaration> {
        return when (this) {
            is UnionTypeDeclaration -> params
            else -> listOf(this)
        }
    }


    private fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): List<MethodNode> {
        return listOf(
                MethodNode(
                        "get",
                        convertParameters(declaration.parameters),
                        declaration.returnType.makeNullable().convertToNode(),
                        emptyList(),
                        false,
                        true,
                        true,
                        null,
                        false
                )
        ) + declaration.returnType.unroll().map { returnType ->
            MethodNode(
                    "set",
                    convertParameters(declaration.parameters + listOf(ParameterDeclaration("value", returnType.convertToNodeNullable()
                            ?: returnType, null, false, false, true))),
                    TypeDeclaration(IdentifierEntity("Unit"), emptyList()),
                    emptyList(),
                    false,
                    true,
                    true,
                    null,
                    false
            )
        }
    }


    private fun CallSignatureDeclaration.convert(): MethodNode {
        return MethodNode(
                "invoke",
                convertParameters(parameters),
                type.convertToNode(),
                convertTypeParameters(typeParameters),
                false,
                true,
                true,
                null,
                false
        )
    }

    private fun convertToHeritageNodes(declarations: List<HeritageClauseDeclaration>): List<HeritageClauseDeclaration> {
        return declarations.map { declaration ->
            HeritageClauseDeclaration(
                    name = declaration.name.convert(),
                    typeArguments = declaration.typeArguments.map { it.convertToNode() },
                    typeReference = declaration.typeReference,
                    extending = declaration.extending
            )
        }
    }

    private fun ClassDeclaration.convert(inDeclaredModule: Boolean): ClassNode {
        return ClassNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member, inDeclaredModule || hasDeclareModifier()) },
                typeParameters.map { typeParameter ->
                    TypeDeclaration(typeParameter.name, typeParameter.constraints.map { it.convertToNode() })
                },
                convertToHeritageNodes(parentEntities),

                uid,
                inDeclaredModule || hasDeclareModifier()
        )
    }

    private fun InterfaceDeclaration.convert(inDeclaredModule: Boolean): TopLevelDeclaration {
        return InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member, true) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                false,
                uid,
                inDeclaredModule || hasDeclareModifier()
        )
    }


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member, true) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                true,
                uid,
                true
        )

        return declaration
    }


    private fun ConstructorDeclaration.convert(): ConstructorNode {
        return ConstructorNode(
                convertParameters(parameters),
                convertTypeParameters(typeParameters),
                body
        )
    }

    private fun TypeAliasDeclaration.convert(): TypeAliasDeclaration {
        return copy(
                aliasName = aliasName,
                typeReference = typeReference.convertToNode()
                )
    }

    private fun FunctionDeclaration.convert(inDeclaredModule: Boolean): FunctionNode {
        return FunctionNode(
                IdentifierEntity(name),
                convertParameters(parameters),
                type.convertToNode(),
                convertTypeParameters(typeParameters),
                hasExportModifier(),
                false,
                false,
                FunctionNodeContextIrrelevant(),
                uid,
                body,
                inDeclaredModule || hasDeclareModifier(),
                isGenerator
        )
    }

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberDeclaration? {
        val memberDeclaration = convertMethodSignatureDeclaration(declaration)
        return when (memberDeclaration) {
            is PropertyNode -> memberDeclaration
            is MethodNode -> memberDeclaration.copy(
                    parameters = memberDeclaration.parameters,
                    type = memberDeclaration.type.convertToNode()
            )
            else -> raiseConcern("unkown method signature") { null }
        }
    }

    fun lowerMemberDeclaration(declaration: MemberEntity, inDeclaredDeclaration: Boolean): List<MemberDeclaration> {
        return when (declaration) {
            is FunctionDeclaration -> listOf(MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type.convertToNode(),
                    convertTypeParameters(declaration.typeParameters),
                    declaration.isStatic(),
                    false,
                    true,
                    declaration.body,
                    declaration.isGenerator
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration)).mapNotNull { it }
            is CallSignatureDeclaration -> listOf(declaration.convert())
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration, inDeclaredDeclaration))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration)
            is ConstructorDeclaration -> listOf(declaration.convert())
            else -> raiseConcern("unkown member declaration ${this}") { emptyList<MemberDeclaration>() }
        }
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration, inDeclaredModule: Boolean): TopLevelDeclaration {
        val type = declaration.type
        return if (type is ObjectLiteralDeclaration) {
            if (type.canBeJson()) {
                VariableNode(
                        IdentifierEntity(declaration.name),
                        TypeDeclaration(IdentifierEntity("Json"), emptyList()),
                        false,
                        emptyList(),
                        declaration.uid,
                        inDeclaredModule || declaration.explicitlyDeclaredType
                )
            } else {
                //TODO: don't forget to create owner
                val objectNode = ObjectNode(
                        IdentifierEntity(declaration.name),
                        type.members.flatMap { member -> lowerMemberDeclaration(member, inDeclaredModule || declaration.hasDeclareModifier()) },
                        emptyList(),
                        declaration.uid,
                        declaration.hasDeclareModifier()
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
                    type.convertToNode(),
                    false,
                    emptyList(),
                    declaration.uid,
                    inDeclaredModule || declaration.explicitlyDeclaredType
            )
        }
    }

    private fun lowerTopLevelDeclaration(declaration: TopLevelEntity, ownerPackageName: NameEntity?, inDeclaredModule: Boolean): TopLevelDeclaration? {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration, inDeclaredModule)
            is FunctionDeclaration -> declaration.convert(inDeclaredModule)
            is ClassDeclaration -> declaration.convert(inDeclaredModule)
            is InterfaceDeclaration -> declaration.convert(inDeclaredModule)
            is GeneratedInterfaceDeclaration -> declaration.convert()
            is ModuleDeclaration -> lowerPackageDeclaration(declaration, ownerPackageName, inDeclaredModule)
            is EnumDeclaration -> declaration
            is TypeAliasDeclaration -> declaration.convert()
            else -> null
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

    @Suppress("UNCHECKED_CAST")
    fun lowerPackageDeclaration(documentRoot: ModuleDeclaration, ownerPackageName: NameEntity?, isDeclaration: Boolean): ModuleNode {
        val name = documentRoot.name

        val shortName = name.unquote()
        val fullPackageName = ownerPackageName?.appendLeft(shortName) ?: shortName

        val imports = mutableMapOf<String, ImportNode>()
        val nonImports = mutableListOf<TopLevelDeclaration>()

        documentRoot.declarations.forEach { declaration ->
            if (declaration is ImportEqualsDeclaration) {
                imports[declaration.name] = ImportNode(
                        declaration.moduleReference.convert(),
                        declaration.uid
                )
            } else {
                lowerTopLevelDeclaration(declaration, fullPackageName, isDeclaration)?.let { nonImports.add(it) }
            }
        }

        return ModuleNode(
                packageName = name,
                qualifiedPackageName = fullPackageName,
                declarations = nonImports,
                imports = imports,
                uid = documentRoot.uid,
                external = isDeclaration
        )
    }
}


class IntroduceNodes : Lowering<SourceSetDeclaration, SourceSetNode> {
    private fun SourceFileDeclaration.introduceNodes(): SourceFileNode {
        val references = root.imports.map { it.referencedFile } + root.references.map { it.referencedFile }

        return SourceFileNode(
                fileName,
                LowerDeclarationsToNodes().lowerPackageDeclaration(root, null, root.kind == ModuleDeclarationKind.DECLARATION_FILE),
                references,
                null
        )
    }

    override fun lower(source: SourceSetDeclaration): SourceSetNode {
        return SourceSetNode(sourceName = source.sourceName, sources = source.sources.map { sourceFile ->
            sourceFile.introduceNodes()
        })
    }
}