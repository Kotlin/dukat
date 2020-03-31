package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration

private fun FunctionTypeDeclaration.convertToCallSignature(typeParams: List<TypeParameterDeclaration>): CallSignatureDeclaration {
    //TODO: consider renaming type param if parent class has a type param with the same name
    return CallSignatureDeclaration(
            parameters = parameters,
            typeParameters = typeParams,
            type = type
    )
}

private class ResolveLambdaParentsLowering(private val topDeclarationResolver: TopLevelDeclarationResolver) : DeclarationTypeLowering {

    override fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): InterfaceDeclaration {

        val callSignaturesFromLambda = mutableListOf<CallSignatureDeclaration>()
        val regularParentEntities = declaration.parentEntities.filter {
            val topDeclaration = topDeclarationResolver.resolve(it.typeReference)
            if ((topDeclaration is TypeAliasDeclaration) && (topDeclaration.typeReference is FunctionTypeDeclaration)) {
                callSignaturesFromLambda.add((topDeclaration.typeReference as FunctionTypeDeclaration).convertToCallSignature(topDeclaration.typeParameters))
                false
            } else {
                true
            }
        }

        val declarationResolved = declaration.copy(
            parentEntities = regularParentEntities,
            members = callSignaturesFromLambda + declaration.members
        )

        return super.lowerInterfaceDeclaration(declarationResolved, owner)
    }
}

private fun SourceSetDeclaration.resolveLambdaParents(topDeclarationResolver: TopLevelDeclarationResolver): SourceSetDeclaration {
    return copy(sources = sources.map { it.copy(root = ResolveLambdaParentsLowering(topDeclarationResolver).lowerDocumentRoot(it.root)) })
}

class ResolveLambdaParents : TsLowering {

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val topDeclarationResolver = TopLevelDeclarationResolver(source)

        return source.resolveLambdaParents(topDeclarationResolver)
    }
}