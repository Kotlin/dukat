package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstantDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstructorDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration


interface IDLLowering {

    fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        return declaration
    }

    fun lowerAttributeDeclaration(declaration: IDLAttributeDeclaration): IDLAttributeDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerArgumentDeclaration(declaration: IDLArgumentDeclaration): IDLArgumentDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerConstantDeclaration(declaration: IDLConstantDeclaration): IDLConstantDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerOperationDeclaration(declaration: IDLOperationDeclaration): IDLOperationDeclaration {
        return declaration.copy(
                returnType = lowerTypeDeclaration(declaration.returnType),
                arguments = declaration.arguments.map { lowerArgumentDeclaration(it) }
        )
    }

    fun lowerConstructorDeclaration(declaration: IDLConstructorDeclaration): IDLConstructorDeclaration {
        return declaration.copy(arguments = declaration.arguments.map { lowerArgumentDeclaration(it) })
    }

    fun lowerTypedefDeclaration(declaration: IDLTypedefDeclaration): IDLTypedefDeclaration {
        return declaration
    }

    fun lowerImplementStatementDeclaration(declaration: IDLImplementsStatementDeclaration): IDLTopLevelDeclaration {
        return declaration.copy(
                child = lowerTypeDeclaration(declaration.child),
                parent = lowerTypeDeclaration(declaration.parent)
        )
    }

    fun lowerDictionaryMemberDeclaration(declaration: IDLDictionaryMemberDeclaration): IDLDictionaryMemberDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration): IDLDictionaryDeclaration {
        return declaration.copy(
                parents = declaration.parents.map { lowerTypeDeclaration(it) as IDLSingleTypeDeclaration },
                members = declaration.members.map { lowerDictionaryMemberDeclaration(it) }
        )
    }

    fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                attributes = declaration.attributes.map { lowerAttributeDeclaration(it) },
                operations = declaration.operations.map { lowerOperationDeclaration(it) },
                constructors = declaration.constructors.map { lowerConstructorDeclaration(it) },
                constants = declaration.constants.map { lowerConstantDeclaration(it) },
                parents = declaration.parents.map { lowerTypeDeclaration(it) as IDLSingleTypeDeclaration },
                primaryConstructor = if (declaration.primaryConstructor == null) {
                    null
                } else {
                    lowerConstructorDeclaration(declaration.primaryConstructor!!)
                }
        )
    }

    fun lowerTopLevelDeclaration(declaration: IDLTopLevelDeclaration): IDLTopLevelDeclaration {
        return when (declaration) {
            is IDLInterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is IDLTypedefDeclaration -> lowerTypedefDeclaration(declaration)
            is IDLImplementsStatementDeclaration -> lowerImplementStatementDeclaration(declaration)
            is IDLDictionaryDeclaration -> lowerDictionaryDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<IDLTopLevelDeclaration>): List<IDLTopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        return fileDeclaration.copy(
                declarations = lowerTopLevelDeclarations(fileDeclaration.declarations)
        )
    }
}