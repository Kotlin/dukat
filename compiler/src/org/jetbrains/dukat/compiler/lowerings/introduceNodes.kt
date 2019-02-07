package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.EnumTokenNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.compiler.converters.convertIndexSignatureDeclaration
import org.jetbrains.dukat.compiler.converters.convertMethodSignatureDeclaration
import org.jetbrains.dukat.compiler.converters.convertPropertyDeclaration
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun FunctionDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

private fun CallSignatureDeclaration.convert(owner: ClassLikeNode): MethodNode {
    return MethodNode(
            "invoke",
            parameters,
            type,
            typeParameters,
            owner,
            false,
            false,
            true,
            listOf(AnnotationNode("nativeInvoke", emptyList())),
            true
    )
}

private fun ParameterValueDeclaration.convertNullable(): ParameterValueDeclaration {
    return when (this) {
        is TypeDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        else -> duplicate()
    }
}

private fun ClassDeclaration.convert(owner: DocumentRootNode?): ClassNode {

    val annotations = mutableListOf<AnnotationNode>()

    if (ModifierDeclaration.hasDefault(modifiers) && ModifierDeclaration.hasExport(modifiers)) {
        annotations.add(AnnotationNode("JsName", listOf("default")))
    }

    return ClassNode(
            name,
            members,
            typeParameters,
            parentEntities,
            null,

            owner,
            uid,
            mutableListOf(),
            annotations
    )
}

private fun InterfaceDeclaration.convert(): InterfaceNode {
    return InterfaceNode(
            name,
            members,
            typeParameters,
            parentEntities,
            mutableListOf(),
            null,
            uid
    )
}

private fun ConstructorDeclaration.convert(owner: ClassLikeNode): ConstructorNode {
    return ConstructorNode(
            parameters,
            typeParameters
    )
}

private fun EnumDeclaration.convert(): EnumNode {
    return EnumNode(
            name = name,
            values = values.map { value -> EnumTokenNode(value.value, value.value) }
    )
}


private class LowerDeclarationsToNodes {

    private fun FunctionDeclaration.convert(): FunctionNode {
        val annotations = mutableListOf<AnnotationNode>()

        val hasExport = ModifierDeclaration.hasExport(modifiers)
        if (ModifierDeclaration.hasDefault(modifiers) && hasExport) {
            annotations.add(AnnotationNode("JsName", listOf("default")))
        }

        return FunctionNode(
                name,
                parameters,
                type,
                typeParameters,
                mutableListOf(),
                annotations,
                hasExport,
                null,
                uid
        )
    }

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: ClassLikeNode): MemberDeclaration {
        val memberDeclaration = convertMethodSignatureDeclaration(declaration, owner)
        return when (memberDeclaration) {
            is PropertyNode -> memberDeclaration
            is MethodNode -> memberDeclaration.copy(
                    parameters = memberDeclaration.parameters,
                    type = memberDeclaration.type
            )
            else -> memberDeclaration
        }
    }

    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: ClassLikeNode): List<MemberDeclaration> {
        return when (declaration) {
            is FunctionDeclaration -> listOf(MethodNode(
                    declaration.name,
                    declaration.parameters,
                    declaration.type,
                    declaration.typeParameters,
                    owner,
                    declaration.isStatic(),
                    false,
                    false,
                    emptyList(),
                    true
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration, owner))
            is CallSignatureDeclaration -> listOf(declaration.convert(owner))
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration, owner))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration, owner)
            is ConstructorDeclaration -> listOf(declaration.convert(owner))
            else -> listOf(declaration)
        }
    }

    fun lowerInterfaceNode(declaration: InterfaceNode): InterfaceNode {
        return declaration.copy(
                members = declaration.members.flatMap { member -> lowerMemberDeclaration(member, declaration) }
        )
    }

    fun lowerClassNode(declaration: ClassNode): ClassNode {
        return declaration.copy(
                members = declaration.members.flatMap { member -> lowerMemberDeclaration(member, declaration) }
        )
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration): TopLevelDeclaration {
        return if (declaration.type is ObjectLiteralDeclaration) {
            //TODO: don't forget to create owner
            val objectNode = ObjectNode(
                    declaration.name,
                    (declaration.type as ObjectLiteralDeclaration).members.flatMap { member -> lowerMemberDeclaration(member, ROOT_CLASS_DECLARATION) },
                    mutableListOf()
            )


            objectNode.copy(members = objectNode.members.map {
                when (it) {
                    is PropertyNode -> it.copy(owner = objectNode, open = false)
                    is MethodNode -> it.copy(owner = objectNode, open = false)
                    else -> it
                }
            })
        } else {
            VariableNode(
                    declaration.name,
                    declaration.type,
                    mutableListOf(),
                    false,
                    null,
                    declaration.uid
            )
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> declaration.convert()
            is ClassDeclaration -> lowerClassNode(declaration.convert(null))
            is InterfaceDeclaration -> lowerInterfaceNode(declaration.convert())
            is DocumentRootDeclaration -> lowerDocumentRoot(declaration, null)
            is EnumDeclaration -> declaration.convert()
            else -> declaration
        }
    }

    fun lowerDocumentRoot(documentRoot: DocumentRootDeclaration, owner: DocumentRootNode?): DocumentRootNode {
        val declarations = documentRoot.declarations.map { declaration -> lowerTopLevelDeclaration(declaration) }

        val imports = mutableMapOf<String, ModuleReferenceDeclaration>()
        val nonImports = mutableListOf<TopLevelDeclaration>()
        declarations.forEach { declaration ->
            if (declaration is ImportEqualsDeclaration) {
                imports.put(declaration.name, declaration.moduleReference)
            } else nonImports.add(declaration)
        }

        val docRoot = DocumentRootNode(
                documentRoot.packageName,
                documentRoot.packageName,
                nonImports,
                imports,
                null,
                documentRoot.uid
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

fun DocumentRootDeclaration.introduceNodes() = LowerDeclarationsToNodes().lowerDocumentRoot(this, null)

