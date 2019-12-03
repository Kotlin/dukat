package org.jetbrains.dukat.idlLowerings

import org.jetbrains.dukat.idlDeclarations.IDLDictionaryMemberDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSingleTypeDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLSourceSetDeclaration
import org.jetbrains.dukat.idlDeclarations.changeComment
import org.jetbrains.dukat.idlDeclarations.processEnumMember
import java.math.BigInteger

private fun String.toBigInteger(): BigInteger {
    return if (startsWith("0x")) {
        BigInteger(substring(2), 16)
    } else {
        BigInteger(this)
    }
}

private class DefaultValueSpecifier(val sourceSet: IDLSourceSetDeclaration) : IDLLowering {

    private fun IDLDictionaryMemberDeclaration.specifyDefaultValue(): String? {
        if (defaultValue == null) {
            return "undefined"
        }
        if (this.type !is IDLSingleTypeDeclaration) {
            return defaultValue
        }
        if (sourceSet.containsEnum(this.type.name)) {
            return "${this.type.name}.${processEnumMember(defaultValue!!)}"
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
            "\$Array", "sequence", "FrozenArray" -> "arrayOf()"
            else -> defaultValue
        }
    }

    override fun lowerDictionaryMemberDeclaration(declaration: IDLDictionaryMemberDeclaration, owner: IDLFileDeclaration): IDLDictionaryMemberDeclaration {
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

fun IDLSourceSetDeclaration.specifyDefaultValues(): IDLSourceSetDeclaration {
    return DefaultValueSpecifier(this).lowerSourceSetDeclaration(this)
}