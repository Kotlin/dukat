package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class GenerateInterfaceReferences(private val astContext: GeneratedInterfacesContext) : DeclarationLowering {

    override fun lowerTypeDeclaration(declaration: TypeDeclaration) = declaration
    override fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration) = declaration
    override fun lowerParameterDeclaration(declaration: ParameterDeclaration) = declaration
    override fun lowerTypeParameter(declaration: TypeParameterDeclaration) = declaration
    override fun lowerUnionTypeDeclation(declaration: UnionTypeDeclaration) = declaration
    override fun lowerIntersectionTypeDeclatation(declaration: IntersectionTypeDeclaration) = declaration
    override fun lowerMemberDeclaration(declaration: MemberDeclaration) = declaration

    // TODO: it looks like we haven't covered interface generattion for interface method signatures
    override fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration) = declaration

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration {
        return declaration.copy(members = declaration.members.map { member -> astContext.lowerMemberDeclaration(member, declaration.uid, declaration.typeParameters) })
    }

    override fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration {
        return declaration.copy(members = declaration.members.map { member -> astContext.lowerMemberDeclaration(member, declaration.uid, declaration.typeParameters) })
    }

    fun lowerParameterValue(declaration: ParameterValueDeclaration, ownerUID: String, typeParameters: List<TypeParameterDeclaration>): ParameterValueDeclaration {
        return astContext.generateInterface(declaration, ownerUID, typeParameters)
    }

    override fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration): TypeAliasDeclaration {
        // TODO: there's should be one way to declare params
        val typeParams = declaration.typeParameters.map { typeParameter -> TypeParameterDeclaration(typeParameter.value, emptyList()) }
        return declaration.copy(typeReference = lowerParameterValue(declaration.typeReference, "${declaration.aliasName}_TYPE", typeParams))
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.map { param ->
                    param.copy(type =  lowerParameterValue(param.type, declaration.uid, declaration.typeParameters))
                },
                type = lowerParameterValue(declaration.type, declaration.uid, declaration.typeParameters)
        )
    }

    override fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return when (declaration.type) {
            is ObjectLiteralDeclaration -> {
                declaration.copy(type = declaration.type.copy(
                        members = declaration.type.members.map { member -> astContext.lowerMemberDeclaration(member, declaration.uid, emptyList()) }
                ))
            }
            else -> declaration
        }
    }

}

fun DocumentRootDeclaration.generateInterfaceReferences(): DocumentRootDeclaration {
    val astContext = GeneratedInterfacesContext()
    return GenerateInterfaceReferences(astContext).lowerDocumentRoot(this).introduceGeneratedEntities(astContext)
}