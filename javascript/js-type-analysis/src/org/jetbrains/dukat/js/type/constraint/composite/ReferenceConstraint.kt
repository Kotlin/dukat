package org.jetbrains.dukat.js.type.constraint.composite

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.PropertyOwnerConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.panic.raiseConcern

data class ReferenceConstraint(
        val identifier: IdentifierEntity,
        val parent: ReferenceConstraint? = null
) : PropertyOwnerConstraint {
    override val propertyNames: Set<String>
        get() = raiseConcern("Properties of a reference should never be listed.") { emptySet() }

    private val modifiedProperties = LinkedHashMap<String, Constraint>()

    override fun set(name: String, data: Constraint) {
        modifiedProperties[name] = data
    }

    override fun get(name: String): Constraint {
        return modifiedProperties[name] ?: ReferenceConstraint(IdentifierEntity(name), this)
    }

    override fun resolve(owner: PropertyOwner): Constraint {
        val referenceOwner = if (parent != null) {
            val resolvedParent = parent.resolve(owner)

            if(resolvedParent is PropertyOwner) {
                resolvedParent
            } else {
                raiseConcern("Accessing property of non-property-owner") {  }
                return CompositeConstraint()
            }
        } else {
            owner
        }

        val dereferencedConstraint = referenceOwner[identifier]

        return if (dereferencedConstraint != null && dereferencedConstraint !is ReferenceConstraint) {
            if(dereferencedConstraint is PropertyOwner) {
                modifiedProperties.forEach { (name, constraint) ->
                    //TODO take composite constraints into account here
                    dereferencedConstraint[name] = constraint
                }
            }

            dereferencedConstraint.resolve(referenceOwner)
        } else {
            this
        }
    }
}