package org.jetbrains.dukat.js.type.constraint.properties

import org.jetbrains.dukat.js.type.constraint.immutable.ImmutableConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner

abstract class PropertyOwnerConstraint(override val owner: PropertyOwner) : PropertyOwner, ImmutableConstraint