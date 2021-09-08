package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.copy
import org.jetbrains.dukat.tsmodel.ConstructSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class RemoveConstructSignatureLowering : DeclarationLowering {
    override fun lowerClassLikeDeclaration(
        declaration: ClassLikeDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        return super.lowerClassLikeDeclaration(
            declaration.copy(newMembers = declaration.members.filterConstructSignatures()),
            owner
        )
    }

    override fun lowerObjectLiteralDeclaration(
        declaration: ObjectLiteralDeclaration,
        owner: NodeOwner<ParameterOwnerDeclaration>?
    ): ParameterValueDeclaration {
        return super.lowerObjectLiteralDeclaration(
            declaration.copy(members = declaration.members.filterConstructSignatures()),
            owner
        )
    }

    private fun List<MemberDeclaration>.filterConstructSignatures(): List<MemberDeclaration> {
        return filter { it !is ConstructSignatureDeclaration }
    }
}

class RemoveConstructSignature : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map {
            it.copy(root = RemoveConstructSignatureLowering().lowerSourceDeclaration(it.root))
        })
    }
}
