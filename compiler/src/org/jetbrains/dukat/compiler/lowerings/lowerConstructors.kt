package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.Declaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.duplicate

fun DocumentRoot.lowerConstructors(): DocumentRoot {
    val loweredDeclarations = declarations.map { declaration ->
        when (declaration) {
            is ClassDeclaration -> {
                var primaryConstructor: MethodDeclaration? = null

                var members: MutableList<MemberDeclaration> = emptyList<MemberDeclaration>().toMutableList()
                for (member in declaration.members) {
                    if ((member is MethodDeclaration) && (member.name == "@@CONSTRUCTOR")) {
                        if (primaryConstructor == null) {
                            primaryConstructor = member
                        } else {
                            members.add(member)
                        }

                    } else {
                        members.add(member)
                    }
                }

                declaration.copy(members = members, primaryConstructor =  primaryConstructor)
            }
            else -> declaration.duplicate<Declaration>()
        }
    }

    return copy(declarations = loweredDeclarations)
}