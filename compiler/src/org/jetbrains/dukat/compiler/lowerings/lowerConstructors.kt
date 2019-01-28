package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.ConstructorDeclaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TopLevelDeclaration
import org.jetbrains.dukat.ast.model.duplicate

fun DocumentRootDeclaration.lowerConstructors(): DocumentRootDeclaration {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is ClassDeclaration -> {
                var primaryConstructor: ConstructorDeclaration? = null

                var members: MutableList<MemberDeclaration> = emptyList<MemberDeclaration>().toMutableList()
                for (member in declaration.members) {
                    if ((member is ConstructorDeclaration)) {
                        if (primaryConstructor == null) {
                            primaryConstructor = member
                        } else {
                            members.add(member)
                        }

                    } else {
                        members.add(member)
                    }
                }

                declaration.copy(members = members, primaryConstructor = primaryConstructor)
            }
            else -> {
                declaration.duplicate<TopLevelDeclaration>()
            }
        }
    }

    return copy(declarations = loweredDeclarations)
}