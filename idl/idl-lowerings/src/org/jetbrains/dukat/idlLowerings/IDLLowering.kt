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

    fun lowerTypeDeclaration(declaration: IDLTypeDeclaration): IDLTypeDeclaration {
        return declaration
    }

    fun lowerAttributeDeclaration(declaration: IDLAttributeDeclaration): IDLAttributeDeclaration {
        return declaration.copy(type = lowerTypeDeclaration(declaration.type))
    }

    fun lowerArgumentDeclaration(declaration: IDLArgumentDeclaration): IDLArgumentDeclaration {
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

    fun lowerImplementStatementDeclaration(declaration: IDLImplementsStatementDeclaration): IDLImplementsStatementDeclaration {
        return declaration.copy(
                child = lowerTypeDeclaration(declaration.child),
                parent = lowerTypeDeclaration(declaration.parent)
        )
    }

    fun lowerIncludesStatementDeclaration(declaration: IDLIncludesStatementDeclaration): IDLIncludesStatementDeclaration {
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

    fun lowerGetterDeclaration(declaration: IDLGetterDeclaration): IDLGetterDeclaration {
        return declaration.copy(
                key = lowerArgumentDeclaration(declaration.key),
                valueType = lowerTypeDeclaration(declaration.valueType)
        )
    }

    fun lowerSetterDeclaration(declaration: IDLSetterDeclaration): IDLSetterDeclaration {
        return declaration.copy(
                key = lowerArgumentDeclaration(declaration.key),
                value = lowerArgumentDeclaration(declaration.value)
        )
    }

    fun lowerInterfaceDeclaration(declaration: IDLInterfaceDeclaration): IDLInterfaceDeclaration {
        return declaration.copy(
                attributes = declaration.attributes.map { lowerAttributeDeclaration(it) },
                operations = declaration.operations.map { lowerOperationDeclaration(it) },
                constructors = declaration.constructors.map { lowerConstructorDeclaration(it) },
                parents = declaration.parents.map { lowerTypeDeclaration(it) as IDLSingleTypeDeclaration },
                primaryConstructor = if (declaration.primaryConstructor == null) {
                    null
                } else {
                    lowerConstructorDeclaration(declaration.primaryConstructor!!)
                },
                getters = declaration.getters.map { lowerGetterDeclaration(it) },
                setters = declaration.setters.map { lowerSetterDeclaration(it) }
        )
    }

    fun lowerEnumDeclaration(declaration: IDLEnumDeclaration): IDLEnumDeclaration {
        return declaration
    }

    fun lowerUnionDeclaration(declaration: IDLUnionDeclaration): IDLUnionDeclaration {
        return declaration.copy(
                unions = declaration.unions.map { lowerTypeDeclaration(it) as IDLSingleTypeDeclaration }
        )
    }

    fun lowerNamespaceDeclaration(declaration: IDLNamespaceDeclaration): IDLNamespaceDeclaration {
        return declaration.copy(
                attributes = declaration.attributes.map { lowerAttributeDeclaration(it) },
                operations = declaration.operations.map { lowerOperationDeclaration(it) }
        )
    }

    fun lowerTopLevelDeclaration(declaration: IDLTopLevelDeclaration): IDLTopLevelDeclaration {
        return when (declaration) {
            is IDLInterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is IDLTypedefDeclaration -> lowerTypedefDeclaration(declaration)
            is IDLImplementsStatementDeclaration -> lowerImplementStatementDeclaration(declaration)
            is IDLDictionaryDeclaration -> lowerDictionaryDeclaration(declaration)
            is IDLEnumDeclaration -> lowerEnumDeclaration(declaration)
            is IDLIncludesStatementDeclaration -> lowerIncludesStatementDeclaration(declaration)
            is IDLNamespaceDeclaration -> lowerNamespaceDeclaration(declaration)
            is IDLUnionDeclaration -> lowerUnionDeclaration(declaration)
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

    fun lowerSourceSetDeclaration(sourceSet: IDLSourceSetDeclaration): IDLSourceSetDeclaration {
        return sourceSet.copy(
                files = sourceSet.files.map { lowerFileDeclaration(it) }
        )
    }
}