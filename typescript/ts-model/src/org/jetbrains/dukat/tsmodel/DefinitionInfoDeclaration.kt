package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity

data class DefinitionInfoDeclaration(
        val uid: String,
        val fileName: String
) : Entity