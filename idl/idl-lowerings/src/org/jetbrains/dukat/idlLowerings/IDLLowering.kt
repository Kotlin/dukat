package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLArgumentDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLConstructorDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLEnumDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLGetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLImplementsStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLIncludesStatementDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLNamespaceDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLOperationDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSetterDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTopLevelDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLTypedefDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLUnionDeclaration


interface IDLLowering {

    fun lowerTypeDeclaration(declaration: IDLTypeDeclaration, owner: IDLFileDeclaration): IDLTypeDeclaration {
        return declaration
    }

    fun lowerAttributeDeclaration(declaration: IDLAttributeDeclaration, owner: IDLFileDeclaration): IDLAttributeDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type, owner))
    }

    fun lowerArgumentDeclaration(declaration: IDLArgumentDeclaration, owner: IDLFileDeclaration): IDLArgumentDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type, owner))
    }

    fun lowerOperationDeclaration(declaration: IDLOperationDeclaration, owner: IDLFileDeclaration): IDLOperationDeclaration {
        return declaration.copy(
                returnType = lowerTypeDeclaration(declaration.returnType, owner),
                arguments = declaration.arguments.map { lowerArgumentDeclaration(it, owner) }
        )
    }

    fun lowerConstructorDeclaration(declaration: IDLConstructorDeclaration, owner: IDLFileDeclaration): IDLConstructorDeclaration {
        return declaration.copy(arguments = declaration.arguments.map { lowerArgumentDeclaration(it, owner) })
    }

    fun lowerTypedefDeclaration(declaration: IDLTypedefDeclaration, owner: IDLFileDeclaration): IDLTypedefDeclaration {
        return declaration
    }

    fun lowerImplementStatementDeclaration(declaration: IDLImplementsStatementDeclaration, owner: IDLFileDeclaration): IDLImplementsStatementDeclaration {
        return declaration.copy(
                child = lowerTypeDeclaration(declaration.child, owner),
                parent = lowerTypeDeclaration(declaration.parent, owner)
        )
    }

    fun lowerIncludesStatementDeclaration(declaration: IDLIncludesStatementDeclaration, owner: IDLFileDeclaration): IDLIncludesStatementDeclaration {
        return declaration.copy(
                child = lowerTypeDeclaration(declaration.child, owner),
                parent = lowerTypeDeclaration(declaration.parent, owner)
        )
    }

    fun lowerDictionaryMemberDeclaration(declaration: IDLDictionaryMemberDeclaration, owner: IDLFileDeclaration): IDLDictionaryMemberDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type, owner))
    }

    fun lowerDictionaryDeclaration(declaration: IDLDictionaryDeclaration, owner: IDLFileDeclaration): IDLDictionaryDeclaration {
        return declaration.copy(
                parents = declaration.parents.map { lowerTypeDeclaration(it, owner) as IDLSingleTypeDeclaration },
                members = declaration.members.map { lowerDictionaryMemberDeclaration(it, owner) }
        )
    }

    fun lowerGetterDeclaration(declaration: IDLGetterDeclaration, owner: IDLFileDeclaration): IDLGetterDeclaration {
        return declaration.copy(
                key = lowerArgumentDeclaration(declaration.key, owner),
                valueType = lowerTypeDeclaration(declaration.valueType, owner)
        )
    }

    fun lowerSetterDeclaration(declaration: IDLSetterDeclaration, owner: IDLFileDeclaration): IDLSetterDeclaration {
        return declaration.copy(
                key = lowerArgumentDeclaration(declaration.key, owner),
                value = lowerArgumentDeclaration(declaration.value, owner)
        )
    }

    fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration, owner: IDLFileDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                attributes = declaration.attributes.map { lowerAttributeDeclaration(it, owner) },
                operations = declaration.operations.map { lowerOperationDeclaration(it, owner) },
                constructors = declaration.constructors.map { lowerConstructorDeclaration(it, owner) },
                parents = declaration.parents.map { lowerTypeDeclaration(it, owner) as IDLSingleTypeDeclaration },
                primaryConstructor = if (declaration.primaryConstructor == null) {
                    null
                } else {
                    lowerConstructorDeclaration(declaration.primaryConstructor!!, owner)
                },
                getters = declaration.getters.map { lowerGetterDeclaration(it, owner) },
                setters = declaration.setters.map { lowerSetterDeclaration(it, owner) }
        )
    }

    fun lowerEnumDeclaration(declaration: IDLEnumDeclaration, owner: IDLFileDeclaration): IDLEnumDeclaration {
        return declaration
    }

    fun lowerUnionDeclaration(declaration: IDLUnionDeclaration, owner: IDLFileDeclaration): IDLUnionDeclaration {
        return declaration.copy(
                unions = declaration.unions.map { lowerTypeDeclaration(it, owner) as IDLSingleTypeDeclaration }
        )
    }

    fun lowerNamespaceDeclaration(declaration: IDLNamespaceDeclaration, owner: IDLFileDeclaration): IDLNamespaceDeclaration {
        return declaration.copy(
                attributes = declaration.attributes.map { lowerAttributeDeclaration(it, owner) },
                operations = declaration.operations.map { lowerOperationDeclaration(it, owner) }
        )
    }

    fun lowerTopLevelDeclaration(declaration: IDLTopLevelDeclaration, owner: IDLFileDeclaration): IDLTopLevelDeclaration {
        return when (declaration) {
            is IDLInterfaceDeclaration -> lowerInterfaceDeclaration(declaration, owner)
            is IDLTypedefDeclaration -> lowerTypedefDeclaration(declaration, owner)
            is IDLImplementsStatementDeclaration -> lowerImplementStatementDeclaration(declaration, owner)
            is IDLDictionaryDeclaration -> lowerDictionaryDeclaration(declaration, owner)
            is IDLEnumDeclaration -> lowerEnumDeclaration(declaration, owner)
            is IDLIncludesStatementDeclaration -> lowerIncludesStatementDeclaration(declaration, owner)
            is IDLNamespaceDeclaration -> lowerNamespaceDeclaration(declaration, owner)
            is IDLUnionDeclaration -> lowerUnionDeclaration(declaration, owner)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<IDLTopLevelDeclaration>, owner: IDLFileDeclaration): List<IDLTopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration, owner)
        }
    }

    fun lowerFileDeclaration(fileDeclaration: IDLFileDeclaration): IDLFileDeclaration {
        return fileDeclaration.copy(
                declarations = lowerTopLevelDeclarations(fileDeclaration.declarations, fileDeclaration)
        )
    }

    fun lowerSourceSetDeclaration(sourceSet: IDLSourceSetDeclaration): IDLSourceSetDeclaration {
        return sourceSet.copy(
                files = sourceSet.files.map { lowerFileDeclaration(it) }
        )
    }
}