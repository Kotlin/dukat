package org.jetbrains.dukat.compiler.converters

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassLikeNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

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

            true
    )
}


fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration, owner: ClassLikeNode): List<MethodNode> {
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
                    listOf(AnnotationNode("nativeGetter")),
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
                    listOf(AnnotationNode("nativeSetter")),
                    true
            )
    )
}


fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: ClassLikeNode): MemberDeclaration {
    return if (declaration.optional) {
        PropertyNode(
                declaration.name,
                FunctionTypeDeclaration(
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
                true
        )
    }
}
