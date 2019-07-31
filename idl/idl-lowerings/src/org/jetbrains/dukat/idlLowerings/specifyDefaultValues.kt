package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.changeComment
import java.math.BigInteger

private fun String.toBigInteger(): BigInteger {
    return if (startsWith("0x")) {
        BigInteger(substring(2), 16)
    } else {
        BigInteger(this)
    }
}

private class DefaultValueSpecifier : IDLLowering {

    private fun IDLDictionaryMemberDeclaration.specifyDefaultValue(): String? {
        if (defaultValue == null) {
            return "undefined"
        }
        if (this.type !is IDLSingleTypeDeclaration) {
            return defaultValue
        }
        return when (this.type.name) {
            "float", "unrestrictedfloat" -> "${defaultValue}f"
            "double", "unrestricteddouble" -> if (defaultValue!!.matches("[0-9]+".toRegex())) {
                "${defaultValue}.0"
            } else {
                defaultValue
            }
            "unsignedlong", "longlong", "unsignedlonglong" -> if (
                    defaultValue!!.toBigInteger() > BigInteger.valueOf(Int.MAX_VALUE.toLong()) ||
                    defaultValue!!.toBigInteger() < BigInteger.valueOf(Int.MIN_VALUE.toLong())) {
                "definedExternally"
            } else {
                defaultValue
            }
            else -> defaultValue
        }
    }

    override fun lowerDictionaryMemberDeclaration(declaration: IDLDictionaryMemberDeclaration): IDLDictionaryMemberDeclaration {
        val specifiedDefaultValue = declaration.specifyDefaultValue()
        return declaration.copy(
                defaultValue = specifiedDefaultValue,
                type = if (declaration.defaultValue != null) {
                    declaration.type.changeComment("= $specifiedDefaultValue")
                } else {
                    declaration.type
                }
        )
    }
}

fun IDLFileDeclaration.specifyDefaultValues(): IDLFileDeclaration {
    return DefaultValueSpecifier().lowerFileDeclaration(this)
}