package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
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
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
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
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
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

fun convertMemberDeclaration(declaration: MemberEntity, inDeclaredDeclaration: Boolean): List<MemberDeclaration> {
    return when (declaration) {
        is MethodDeclaration -> listOf(
                declaration.copy(
                        type = declaration.type.convertToNode(),
                        parameters = convertParameters(declaration.parameters)
                )
        )
        is MethodSignatureDeclaration -> listOf(convertMethodSignatureDeclaration(declaration)).map { it }
        is CallSignatureDeclaration -> listOf(declaration.convert())
        is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration, inDeclaredDeclaration))
        is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration)
        is ConstructorDeclaration -> listOf(declaration.convert())
        else -> raiseConcern("unkown member declaration ${declaration::class.java}") { emptyList<MemberDeclaration>() }
    }
}

private fun convertParameters(parameters: List<ParameterDeclaration>): List<ParameterDeclaration> {
    return parameters.map { param -> param.convertToNode() }
}

private fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberDeclaration {
    return declaration.copy(
            type = declaration.type.convertToNode(),
            parameters = convertParameters(declaration.parameters)
    )
}

fun convertPropertyDeclaration(declaration: PropertyDeclaration, inDeclaredDeclaration: Boolean): PropertyDeclaration {
    val parameterValueDeclaration = if (declaration.optional) declaration.type.makeNullable() else declaration.type
    return declaration.copy(
            type = parameterValueDeclaration.convertToNode(),
            explicitlyDeclaredType = inDeclaredDeclaration || declaration.explicitlyDeclaredType
    )
}

private fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): List<IndexSignatureDeclaration> {
    return listOf(
            declaration.copy(
                    parameters = convertParameters(declaration.parameters)
            )
    )
}

private fun CallSignatureDeclaration.convert(): CallSignatureDeclaration {
    return copy(
            parameters = convertParameters(parameters),
            type = type.convertToNode()
    )
}

private fun ConstructorDeclaration.convert(): ConstructorDeclaration {
    return copy(
            parameters = convertParameters(parameters)
    )
}


private class LowerDeclarationsToNodes(private val rootIsDeclaration: Boolean) {


    private fun convertTypeParameters(typeParams: List<TypeParameterDeclaration>): List<TypeDeclaration> {
        return typeParams.map { typeParam ->
            TypeDeclaration(
                    value = typeParam.name,
                    params = typeParam.constraints.map { it.convertToNode() }
            )
        }
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

    private fun InterfaceDeclaration.convert(): InterfaceDeclaration {
        return copy(
                members = members.flatMap { member -> convertMemberDeclaration(member, true) },
                parentEntities = convertToHeritageNodes(parentEntities)
        )
    }


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceDeclaration {
        return InterfaceDeclaration(
                name = name,
                members = members.flatMap { member -> convertMemberDeclaration(member, true) },
                typeParameters = typeParameters,
                parentEntities = convertToHeritageNodes(parentEntities),
                uid = uid,
                modifiers = emptySet(),
                definitionsInfo = emptyList()
        )
    }

    private fun TypeAliasDeclaration.convert(): TypeAliasDeclaration {
        return copy(
                aliasName = aliasName,
                typeReference = typeReference.convertToNode()
                )
    }

    private fun FunctionDeclaration.convert(): FunctionDeclaration {
        return copy(
                parameters = convertParameters(parameters),
                type = type.convertToNode(),
                uid = uid,
                body = body,
                isGenerator = isGenerator
        )
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        val type = declaration.type
        return declaration.copy(
                type = type.convertToNode(),
                explicitlyDeclaredType = rootIsDeclaration || declaration.explicitlyDeclaredType
        )
    }

    private fun lowerTopLevelDeclaration(declaration: TopLevelEntity, ownerPackageName: NameEntity?): TopLevelDeclaration? {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> declaration.convert()
            is ClassDeclaration -> declaration.copy(
                members = declaration.members.flatMap { member -> convertMemberDeclaration(member, rootIsDeclaration || declaration.hasDeclareModifier()) },
                parentEntities = convertToHeritageNodes(declaration.parentEntities)
            )
            is InterfaceDeclaration -> declaration.convert()
            is GeneratedInterfaceDeclaration -> declaration.convert()
            is ModuleDeclaration -> lowerPackageDeclaration(declaration, ownerPackageName)
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
    fun lowerPackageDeclaration(documentRoot: ModuleDeclaration, ownerPackageName: NameEntity?): ModuleNode {
        val name = documentRoot.name

        val shortName = name.unquote()
        val fullPackageName = ownerPackageName?.appendLeft(shortName) ?: shortName

        val declarations = documentRoot.declarations.mapNotNull { declaration ->
                lowerTopLevelDeclaration(declaration, fullPackageName)
        }

        return ModuleNode(
                packageName = name,
                qualifiedPackageName = fullPackageName,
                declarations = declarations,
                uid = documentRoot.uid,
                kind  = documentRoot.kind
        )
    }
}


class IntroduceNodes : Lowering<SourceSetDeclaration, SourceSetNode> {
    private fun SourceFileDeclaration.introduceNodes(): SourceFileNode {
        val references = root.imports.map { it.referencedFile } + root.references.map { it.referencedFile }

        return SourceFileNode(
                fileName,
                LowerDeclarationsToNodes(root.kind == ModuleDeclarationKind.DECLARATION_FILE).lowerPackageDeclaration(root, null),
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