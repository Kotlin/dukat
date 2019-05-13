package org.jetbrains.dukat.tsmodel.lowerings

import org.jetbrains.dukat.astCommon.AstMemberEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration


private class GenerateInterfaceReferences() : DeclarationWithOwnerLowering {

    private val myAstContext: GeneratedInterfacesContext = GeneratedInterfacesContext()

    override fun lowerTypeDeclaration(owner: NodeOwner<TypeDeclaration>) = owner.node
    override fun lowerFunctionTypeDeclaration(owner: NodeOwner<FunctionTypeDeclaration>) = owner.node
    override fun lowerParameterDeclaration(owner: NodeOwner<ParameterDeclaration>) = owner.node
    override fun lowerUnionTypeDeclation(owner: NodeOwner<UnionTypeDeclaration>) = owner.node
    override fun lowerIntersectionTypeDeclatation(owner: NodeOwner<IntersectionTypeDeclaration>) = owner.node
    override fun lowerMemberDeclaration(owner: NodeOwner<AstMemberEntity>) = owner.node

    fun getContext(): GeneratedInterfacesContext {
        return myAstContext
    }

    override fun lowerTypeParameter(owner: NodeOwner<TypeParameterDeclaration>) = owner.node


    // TODO: it looks like we haven't covered interface generattion for interface method signatures
    override fun lowerMethodSignatureDeclaration(owner: NodeOwner<MethodSignatureDeclaration>) = owner.node

    override fun lowerInterfaceDeclaration(owner: NodeOwner<InterfaceDeclaration>): InterfaceDeclaration {
        val declaration = owner.node
        return declaration.copy(members = declaration.members.map { member -> myAstContext.lowerMemberDeclaration(owner.wrap(member), declaration.uid, declaration.typeParameters) })
    }

    override fun lowerClassDeclaration(owner: NodeOwner<ClassDeclaration>): ClassDeclaration {
        val declaration = owner.node
        return declaration.copy(members = declaration.members.map { member -> myAstContext.lowerMemberDeclaration(owner.wrap(member), declaration.uid, declaration.typeParameters) })
    }

    fun lowerParameterValue(owner: NodeOwner<ParameterValueDeclaration>, ownerUID: String, typeParameters: List<TypeParameterDeclaration>): ParameterValueDeclaration {
        return myAstContext.generateInterface(owner, ownerUID, typeParameters)
    }

    override fun lowerTypeAliasDeclaration(owner: NodeOwner<TypeAliasDeclaration>): TypeAliasDeclaration {
        val declaration = owner.node
        // TODO: there's should be one way to declare params
        val typeParams = declaration.typeParameters.map { typeParameter -> TypeParameterDeclaration(typeParameter, emptyList()) }
        return declaration.copy(typeReference = lowerParameterValue(owner.wrap(declaration.typeReference), declaration.uid, typeParams))
    }

    override fun lowerFunctionDeclaration(owner: NodeOwner<FunctionDeclaration>): FunctionDeclaration {
        val declaration = owner.node
        return declaration.copy(
                parameters = declaration.parameters.map { param ->
                    param.copy(type = lowerParameterValue(owner.wrap(param.type), declaration.uid, declaration.typeParameters))
                },
                type = lowerParameterValue(owner.wrap(declaration.type), declaration.uid, declaration.typeParameters)
        )
    }

    override fun lowerVariableDeclaration(owner: NodeOwner<VariableDeclaration>): VariableDeclaration {
        val declaration = owner.node
        return when (declaration.type) {
            is ObjectLiteralDeclaration -> {
                declaration.copy(type = declaration.type.copy(
                        members = declaration.type.members.map { member -> myAstContext.lowerMemberDeclaration(owner.wrap(member), declaration.uid, emptyList()) }
                ))
            }
            else -> declaration
        }
    }

    override fun lowerDocumentRoot(documentRoot: PackageDeclaration, owner: NodeOwner<PackageDeclaration>): PackageDeclaration {

        val declarations = documentRoot.declarations.map { declaration ->
            when (declaration) {
                !is TypeAliasDeclaration -> lowerTopLevelDeclaration(owner.wrap(declaration))
                else -> declaration
            }
        }.map { declaration ->
            when (declaration) {
                is TypeAliasDeclaration -> lowerTopLevelDeclaration(owner.wrap(declaration))
                else -> declaration
            }
        }

        return documentRoot.copy(declarations = declarations)
    }
}

fun PackageDeclaration.generateInterfaceReferences(): PackageDeclaration {
    val generateInterfaceReferences = GenerateInterfaceReferences()
    return generateInterfaceReferences.lowerDocumentRoot(this, NodeOwner(this, null)).introduceGeneratedEntities(generateInterfaceReferences.getContext())
}

fun SourceFileDeclaration.generateInterfaceReferences() = copy(root = root.generateInterfaceReferences())

fun SourceSetDeclaration.generateInterfaceReferences() = copy(sources = sources.map(SourceFileDeclaration::generateInterfaceReferences))