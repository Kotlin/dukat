package org.jetbrains.dukat.ast.model.nodes

import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

data class FunctionNode(
        val name: NameEntity,
        val parameters: List<ParameterDeclaration>,
        val type: ParameterValueDeclaration,
        val typeParameters: List<TypeDeclaration>,

        val export: Boolean,
        val uid: String,

        val body: BlockDeclaration?,
        val external: Boolean,
        val isGenerator: Boolean
) : MemberEntity, TopLevelDeclaration