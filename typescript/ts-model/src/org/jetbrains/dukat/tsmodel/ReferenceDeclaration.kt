package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.ReferenceEntity

data class ReferenceDeclaration(override val uid: String, val origin: ReferenceOriginDeclaration = ReferenceOriginDeclaration.IRRELEVANT) : ReferenceEntity
