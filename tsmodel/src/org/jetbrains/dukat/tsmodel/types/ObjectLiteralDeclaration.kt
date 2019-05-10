package org.jetbrains.dukat.tsmodel.types

import org.jetbrains.dukat.astCommon.AstMemberEntity

data class ObjectLiteralDeclaration(
        val members: List<AstMemberEntity>,
        override var nullable: Boolean = false,
        override var meta: ParameterValueDeclaration? = null
) : ParameterValueDeclaration, AstMemberEntity

fun ObjectLiteralDeclaration.canBeJson(): Boolean {
    if (members.size != 1) {
        return false
    }

    if (members.get(0) !is IndexSignatureDeclaration) {
        return false
    }

    val indexSignatureDeclaration = members.get(0) as IndexSignatureDeclaration
    if (indexSignatureDeclaration.indexTypes.size != 1) {
        return false
    }

    val indexType = indexSignatureDeclaration.indexTypes.get(0)

    if (indexType.type !is TypeDeclaration) {
        return false
    }

    val typeDeclaration = indexType.type

    return (typeDeclaration.isSimpleType("string"))
            && (indexSignatureDeclaration.returnType.isSimpleType("any"))
}
