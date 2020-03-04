package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private class NameUnnamedFunctionParameters : DeclarationTypeLowering {

    private fun Int.asLetter() = 'A' + (this % 26)

    private fun Int.toParameterName() : String {
        var num = this
        var name = num.asLetter().toString()
        while (num >= 26) {
            num = (num - 26) / 26
            name = num.asLetter() + name
        }

        return "arg$name"
    }

    private fun List<ParameterDeclaration>.lowerParameters(parameterOwner: NodeOwner<ParameterOwnerDeclaration>) : List<ParameterDeclaration> {
        var argumentId = 0

        return this.map { parameter ->
            if (parameter.name.isEmpty()) {
                parameter.copy(
                        name = argumentId++.toParameterName(),
                        type = lowerParameterValue(parameter.type, parameterOwner)
                )
            } else {
                lowerParameterDeclaration(parameter, parameterOwner)
            }
        }
    }

    override fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<FunctionOwnerDeclaration>): FunctionDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.lowerParameters(owner.wrap(declaration)),
                type = lowerParameterValue(declaration.type, owner.wrap(declaration))
        )
    }

    override fun lowerCallSignatureDeclaration(declaration: CallSignatureDeclaration, owner: NodeOwner<MemberDeclaration>): CallSignatureDeclaration {
        return declaration.copy(
                parameters = declaration.parameters.lowerParameters(owner.wrap(declaration)),
                type = lowerParameterValue(declaration.type, owner.wrap(declaration))
        )
    }

}

fun ModuleDeclaration.nameUnnamedFunctionParameters(): ModuleDeclaration {
    return NameUnnamedFunctionParameters().lowerDocumentRoot(this)
}

fun SourceFileDeclaration.nameUnnamedFunctionParameters() = copy(root = root.nameUnnamedFunctionParameters())

fun SourceSetDeclaration.nameUnnamedFunctionParameters() = copy(sources = sources.map(SourceFileDeclaration::nameUnnamedFunctionParameters))