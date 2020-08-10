package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private class RemoveThisParametersLowering : DeclarationLowering {

    fun removeThisParam(parameters: List<ParameterDeclaration>): List<ParameterDeclaration> {
        return if (parameters.firstOrNull()?.name == "this") {
            parameters.drop(1)
        } else {
            parameters
        }
    }

    override fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration, owner: NodeOwner<MemberDeclaration>?): MethodSignatureDeclaration {
        return super.lowerMethodSignatureDeclaration(declaration.copy(parameters = removeThisParam(declaration.parameters)), owner)
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<ModuleDeclaration>?): FunctionDeclaration {
        return super.lowerFunctionDeclaration(declaration.copy(parameters = removeThisParam(declaration.parameters)), owner)
    }
}

class RemoveThisParameters : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(root = RemoveThisParametersLowering().lowerSourceDeclaration(sourceFileDeclaration.root))
        })
    }
}