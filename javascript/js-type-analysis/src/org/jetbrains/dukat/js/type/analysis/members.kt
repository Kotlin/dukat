package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ClassConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration

@Suppress("UNUSED")
fun ConstructorDeclaration.addTo(@Suppress("UNUSED_PARAMETER") owner: PropertyOwner) {
    raiseConcern("Cannot create header for constructor.") {  } //TODO add constructor body to AST
}

fun PropertyDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    owner[name] = initializer?.calculateConstraints(owner, path) ?: NoTypeConstraint
}

fun MemberDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        is PropertyDeclaration -> this.addTo(owner, path)
        else -> raiseConcern("Unexpected member entity type <${this::class}> in object.") { this }
    }
}

fun MemberDeclaration.addToClass(owner: ClassConstraint, path: PathWalker) {
    when (this) {
        is ConstructorDeclaration -> this.addTo(owner)
        is FunctionDeclaration -> if (this.modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)) {
            this.addTo(owner)
        } else {
            val ownerPrototype = owner["prototype"]

            if (ownerPrototype is PropertyOwner) {
                this.addTo(ownerPrototype)
            } else {
                raiseConcern("Class prototype corrupt! Cannot add members.") {  }
            }
        }
        is PropertyDeclaration -> this.addTo(owner, path)
        else -> raiseConcern("Unexpected member entity type <${this::class}> in class.") { this }
    }
}