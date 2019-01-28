package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.CallSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.MethodSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.PropertyDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.IndexSignatureDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.duplicate
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode


private fun FunctionDeclaration.isStatic() =
        modifiers.find { it.token == "STATIC" } != null

private fun PropertyDeclaration.isStatic() =
        modifiers.find { it.token == "STATIC" } != null


private fun ParameterValueDeclaration.makeNullable(): ParameterValueDeclaration {
    return when (this) {
        is TypeDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        else -> throw Exception("makeNullable does not recognize type ${this}")
    }
}

private fun PropertyDeclaration.convert(owner: ClassLikeDeclaration): PropertyNode {
    return PropertyNode(
            name,
            if (optional) type.makeNullable() else type,
            typeParameters,

            owner,
            isStatic(),
            false,
            optional,
            optional  // TODO: it's actually wrong
    )
}

private fun CallSignatureDeclaration.convert(owner: ClassLikeDeclaration): MethodNode {
    return MethodNode(
            "invoke",
            parameters,
            type,
            typeParameters,
            owner,
            false,
            false,
            true,
            listOf(AnnotationNode("nativeInvoke")))
}

private fun ParameterValueDeclaration.convertNullable(): ParameterValueDeclaration {
    return when(this) {
        is TypeDeclaration -> copy(nullable = true)
        is FunctionTypeDeclaration -> copy(nullable = true)
        else -> duplicate()
    }
}

private fun IndexSignatureDeclaration.convert(owner: ClassLikeDeclaration): List<MethodNode> {
    return listOf(
            MethodNode(
                    "get",
                    indexTypes,
                    returnType.makeNullable(),
                    emptyList(),
                    owner,
                    false,
                    false,
                    true,
                    listOf(AnnotationNode("nativeGetter"))
            ),
            MethodNode(
                    "set",
                    indexTypes.toMutableList() + listOf(ParameterDeclaration("value", returnType, null, false)),
                    TypeDeclaration("Unit", emptyList()),
                    emptyList(),
                    owner,
                    false,
                    false,
                    true,
                    listOf(AnnotationNode("nativeSetter"))
            )
    )
}


private fun MethodSignatureDeclaration.convert(owner: ClassLikeDeclaration): MemberDeclaration {
    return if (optional) {
        PropertyNode(
                name,
                FunctionTypeDeclaration(
                        parameters,
                        type,
                        true,
                        null
                ),
                typeParameters,
                owner,
                false,
                false,
                true,
                false
        )
    } else {
        MethodNode(
                name,
                parameters,
                type,
                typeParameters,
                owner,
                false, //TODO: remove static, we don't need it for MethodSignatures
                false,
                false,
                emptyList()
        )
    }
}

private class LowerFunctionsInClassLike : Lowering {
    override fun lowerVariableDeclaration(declaration: VariableDeclaration) = declaration
    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration) = declaration
    override fun lowerTypeDeclaration(declaration: TypeDeclaration) = declaration
    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration) = declaration
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration) = declaration
    override fun lowerTypeParameter(declaration: TypeParameterDeclaration) = declaration
    override fun lowerObjectLiteral(declaration: ObjectLiteralDeclaration) = declaration

    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: ClassLikeDeclaration): List<MemberDeclaration> {
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
                    emptyList()
            ))
            is MethodSignatureDeclaration -> listOf(declaration.convert(owner))
            is CallSignatureDeclaration -> listOf(declaration.convert(owner))
            is PropertyDeclaration -> listOf(declaration.convert(owner))
            is IndexSignatureDeclaration -> declaration.convert(owner)
            else -> listOf(lowerMemberDeclaration(declaration))
        }
    }

    override fun lowerMemberDeclaration(declaration: MemberDeclaration) = declaration

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {
        return declaration.copy(
                members = declaration.members.flatMap { member -> lowerMemberDeclaration(member, declaration) }
        )
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration {
        return declaration.copy(
                members = declaration.members.flatMap { member -> lowerMemberDeclaration(member, declaration) }
        )
    }
}

fun DocumentRootDeclaration.introduceMemberNodes(): DocumentRootDeclaration {
    return LowerFunctionsInClassLike().lowerDocumentRoot(this)
}
