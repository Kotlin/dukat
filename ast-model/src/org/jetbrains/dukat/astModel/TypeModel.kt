package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity

interface TypeModel : Entity {
    val nullable: Boolean
}