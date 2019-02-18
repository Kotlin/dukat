package org.jetbrains.dukat.compiler.lowerings.nodeIntroduction

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.EnumTokenNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.HeritageSymbolNode
import org.jetbrains.dukat.ast.model.nodes.IdentifierNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyAccessNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.compiler.model.ROOT_CLASS_DECLARATION
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
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
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private class LowerDeclarationsToNodes {

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

    private fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: ClassLikeNode): MemberNode {
        return if (declaration.optional) {
            PropertyNode(
                    declaration.name,
                    FunctionTypeNode(
                            declaration.parameters,
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
                    declaration.parameters,
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
                        declaration.indexTypes,
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
                        declaration.indexTypes.toMutableList() + listOf(ParameterDeclaration("value", declaration.returnType, null, false, false)),
                        TypeDeclaration("Unit", emptyList()),
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
                parameters,
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
        return when(this) {
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
            annotations.add(AnnotationNode("JsName", listOf("default")))
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

    private fun InterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters,
                convertToHeritageNodes(parentEntities),
                mutableListOf(),
                null,
                uid
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


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters,
                convertToHeritageNodes(parentEntities),
                mutableListOf(),
                null,
                InterfaceNode.AUTOGENERATED
        )


        declaration.members.forEach { member ->
            when (member) {
                is PropertyNode -> member.copy(owner = declaration)
                is MethodNode -> member.copy(owner = declaration)
            }
        }

        return declaration
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
                values = values.map { value -> EnumTokenNode(value.value, value.meta) }
        )
    }

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

    fun lowerMemberDeclaration(declaration: MemberDeclaration): List<MemberNode> {
        val owner = ROOT_CLASS_DECLARATION
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
                    true,
                    true
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration, owner))
            is CallSignatureDeclaration -> listOf(declaration.convert(owner))
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration, owner))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration, owner)
            is ConstructorDeclaration -> listOf(declaration.convert(owner))
            else -> throw Exception("unkown member declaration ${this}")
        }
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration): TopLevelDeclaration {
        val type = declaration.type
        return if (type is ObjectLiteralDeclaration) {
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
        } else {
            VariableNode(
                    declaration.name,
                    type,
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
            is ClassDeclaration -> declaration.convert(null)
            is InterfaceDeclaration -> declaration.convert()
            is GeneratedInterfaceDeclaration -> declaration.convert()
            is DocumentRootDeclaration -> lowerDocumentRoot(declaration, null)
            is EnumDeclaration -> declaration.convert()
            else -> declaration
        }
    }


    // TODO: introduce ModuleReferenceNode
    private fun ModuleReferenceDeclaration.convert() : ModuleReferenceDeclaration {
        return when(this) {
            is IdentifierDeclaration -> IdentifierNode(value)
            else -> this
        }
    }

    fun lowerDocumentRoot(documentRoot: DocumentRootDeclaration, owner: DocumentRootNode?): DocumentRootNode {
        val declarations = documentRoot.declarations.map { declaration -> lowerTopLevelDeclaration(declaration) }

        val imports = mutableMapOf<String, ModuleReferenceDeclaration>()
        val nonImports = mutableListOf<TopLevelDeclaration>()
        declarations.forEach { declaration ->
            if (declaration is ImportEqualsDeclaration) {
                imports.put(declaration.name, declaration.moduleReference.convert())
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

