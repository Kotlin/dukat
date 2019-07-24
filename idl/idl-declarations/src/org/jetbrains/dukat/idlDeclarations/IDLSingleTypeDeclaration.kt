package org.jetbrains.dukat.idlDeclarations

data class IDLSingleTypeDeclaration(
        override val name: String,
        val typeParameter: IDLTypeDeclaration?,
        override val nullable: Boolean
) : IDLTypeDeclaration

fun IDLSingleTypeDeclaration.isPrimitive(): Boolean {
    return name in listOf(
            "void",
            "float",
            "unrestrictedfloat",
            "double",
            "unrestricteddouble",
            "long",
            "unsignedlong",
            "longlong",
            "unsignedlonglong",
            "octet",
            "byte",
            "short",
            "unsignedshort",
            "boolean",
            "ByteString",
            "DOMString",
            "USVString",
            "\$Array",
            "sequence",
            "object",
            "DOMError",
            "\$dynamic",
            "any"
    )
}