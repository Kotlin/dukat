package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
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

    override fun lowerVariableDeclaration(declaration: VariableDeclaration) = declaration
    override fun lowerTypeDeclaration(declaration: TypeDeclaration) = declaration
    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration) = declaration
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration) = declaration
    override fun lowerTypeParameter(declaration: TypeParameterDeclaration) = declaration
    override fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration) = declaration
    override fun lowerIntersectionTypeDeclatation(declaration: IntersectionTypeDeclaration) = declaration
    override fun lowerMemberDeclaration(declaration: MemberDeclaration) = declaration
    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration) = declaration

    private fun ClassLikeDeclaration.getUID(): String {
        return when (this) {
            is ClassDeclaration -> uid
            is InterfaceDeclaration -> uid
            else -> throw Exception("can not create uid for ${this}")
        }
    }

    private fun FunctionDeclaration.getUID(): String = uid

    private fun ParameterValueDeclaration.generateInterface(owner: ClassLikeDeclaration): ParameterValueDeclaration {
        return when (this) {
            is ObjectLiteralDeclaration -> {
                if (members.isEmpty()) {
                    TypeDeclaration("Any", emptyList())
                } else {
                    val referenceNode = astContext.registerObjectLiteralDeclaration(this, owner.getUID())
                    referenceNode
                }
            }
            else -> this
        }
    }

    private fun ParameterValueDeclaration.generateInterface(owner: FunctionDeclaration): ParameterValueDeclaration {
        return when (this) {
            is ObjectLiteralDeclaration -> {
                if (members.isEmpty()) {
                    TypeDeclaration("Any", emptyList())
                } else {
                    val referenceNode = astContext.registerObjectLiteralDeclaration(this, owner.getUID())
                    referenceNode
                }
            }
            else -> this
        }
    }


    fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: ClassLikeDeclaration): MemberDeclaration {
        return when (declaration) {
            is ConstructorDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(owner))
                    }
            )
            is PropertyDeclaration -> declaration.copy(type = declaration.type.generateInterface(owner))
            is MethodSignatureDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(owner))
                    },
                    type = declaration.type.generateInterface(owner)
            )
            is FunctionDeclaration -> declaration.copy(
                    parameters = declaration.parameters.map { param ->
                        param.copy(type = param.type.generateInterface(owner))
                    },
                    type = declaration.type.generateInterface(owner)
            )

            else -> declaration
        }
    }

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, declaration) })
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration {
        return declaration.copy(members = declaration.members.map { member -> lowerMemberDeclaration(member, declaration) })
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param ->
                    param.copy(type = param.type.generateInterface(declaration))
                },
                type = declaration.type.generateInterface(declaration)
        )
    }
}

fun DocumentRootDeclaration.generateInterfaceReferences(): DocumentRootDeclaration {
    val astContext = TsAstContext()
    return GenerateInterfaceReferences(astContext).lowerDocumentRoot(this).introduceGeneratedEntities(astContext)
}