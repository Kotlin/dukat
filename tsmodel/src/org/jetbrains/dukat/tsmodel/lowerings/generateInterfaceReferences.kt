package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class GenerateInterfaceReferences(private val astContext: TsAstContext) : DeclarationLowering {

    override fun lowerTypeDeclaration(declaration: TypeDeclaration) = declaration
    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration) = declaration
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration) = declaration
    override fun lowerTypeParameter(declaration: TypeParameterDeclaration) = declaration
    override fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration) = declaration
    override fun lowerIntersectionTypeDeclatation(declaration: IntersectionTypeDeclaration) = declaration
    override fun lowerMemberDeclaration(declaration: MemberDeclaration) = declaration
    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration) = declaration

    private fun ParameterValueDeclaration.generateInterface(ownerUID: String): ParameterValueDeclaration {
        return when (this) {
            is ObjectLiteralDeclaration -> {
                if (members.isEmpty()) {
                    TypeDeclaration("Any", emptyList())
                } else {
                    val referenceNode = astContext.registerObjectLiteralDeclaration(this, ownerUID)
                    referenceNode
                }
            }
            else -> this
        }
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return when(declaration.type) {
            is ObjectLiteralDeclaration -> {
                declaration.copy(type = declaration.type.copy(
                        members = declaration.type.members.map { member -> lowerMemberDeclaration(member, declaration.uid) }
                ))
            }
            else -> declaration
        }
    }

    fun lowerMemberDeclaration(declaration: MemberDeclaration, ownerUid: String): MemberDeclaration {
        return when (declaration) {
            is CallSignatureDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(ownerUid))
                    },
                    type = declaration.type.generateInterface(ownerUid)
            )
            is ConstructorDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(ownerUid))
                    }
            )
            is PropertyDeclaration -> declaration.copy(type = declaration.type.generateInterface(ownerUid))
            is MethodSignatureDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(ownerUid))
                    },
                    type = declaration.type.generateInterface(ownerUid)
            )
            is FunctionDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(ownerUid))
                    },
                    type = declaration.type.generateInterface(ownerUid)
            )

            else -> declaration
        }
    }

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, declaration.uid) })
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, declaration.uid) })
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param ->
                    param.copy(type = param.type.generateInterface(declaration.uid))
                },
                type = declaration.type.generateInterface(declaration.uid)
        )
    }
}

fun DocumentRootDeclaration.generateInterfaceReferences(): DocumentRootDeclaration {
    val astContext = TsAstContext()
    return GenerateInterfaceReferences(astContext).lowerDocumentRoot(this).introduceGeneratedEntities(astContext)
}