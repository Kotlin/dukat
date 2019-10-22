package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration

internal fun ModuleDeclaration.collectReferences(references: MutableMap<String, ClassLikeDeclaration> = mutableMapOf()): Map<String, ClassLikeDeclaration> {
    declarations.forEach {
        if (it is ClassDeclaration) {
            references[it.uid] = it
        } else if (it is InterfaceDeclaration) {
            references[it.uid] = it
        } else if (it is ModuleDeclaration) {
            it.collectReferences(references)
        }
    }

    return references
}
