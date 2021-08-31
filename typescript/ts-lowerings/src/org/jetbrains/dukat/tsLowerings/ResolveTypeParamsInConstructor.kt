package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration

class ResolveTypeParamsInConstructor(
    private val typesMapping: Map<NameEntity, ParameterValueDeclaration>
) : DeclarationLowering {
    override fun lowerTypeParamReferenceDeclaration(declaration: TypeParamReferenceDeclaration): ParameterValueDeclaration =
        typesMapping[declaration.value] ?: super.lowerTypeParamReferenceDeclaration(declaration)
}