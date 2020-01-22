package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MemberOwnerDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

private class ReplaceFunctionTypeVariablesWithFunctions : DeclarationTypeLowering {

    fun PropertyDeclaration.replaceFunctionTypeVariablesWithFunctions(): MemberDeclaration {
        return when (val type = type) {
            is FunctionTypeDeclaration -> FunctionDeclaration(
                    name = name,
                    parameters = type.parameters,
                    type = type.type,
                    typeParameters = emptyList(),
                    modifiers = modifiers,
                    body = null,
                    uid = ""
            )
            else -> this
        }
    }

    fun VariableDeclaration.replaceFunctionTypeVariablesWithFunctions(): TopLevelDeclaration {
        return when (val type = type) {
            is FunctionTypeDeclaration -> FunctionDeclaration(
                    name = name,
                    parameters = type.parameters,
                    type = type.type,
                    typeParameters = emptyList(),
                    modifiers = modifiers,
                    body = null,
                    uid = ""
            )
            else -> this
        }
    }

    override fun lowerMemberDeclaration(declaration: MemberDeclaration, owner: NodeOwner<MemberOwnerDeclaration>): MemberDeclaration {
        val convertedDeclaration = when (declaration) {
            is PropertyDeclaration -> declaration.replaceFunctionTypeVariablesWithFunctions()
            else -> declaration
        }

        return super.lowerMemberDeclaration(convertedDeclaration, owner)
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration, owner: NodeOwner<ModuleDeclaration>): TopLevelDeclaration {
        val convertedDeclaration = when (declaration) {
            is VariableDeclaration -> declaration.replaceFunctionTypeVariablesWithFunctions()
            else -> declaration
        }

        return super.lowerTopLevelDeclaration(convertedDeclaration, owner)
    }
}

fun ModuleDeclaration.replaceFunctionTypeVariablesWithFunctions(): ModuleDeclaration {
    return ReplaceFunctionTypeVariablesWithFunctions().lowerDocumentRoot(this)
}

fun SourceFileDeclaration.replaceFunctionTypeVariablesWithFunctions() = copy(root = root.replaceFunctionTypeVariablesWithFunctions())

fun SourceSetDeclaration.replaceFunctionTypeVariablesWithFunctions() = copy(sources = sources.map(SourceFileDeclaration::replaceFunctionTypeVariablesWithFunctions))