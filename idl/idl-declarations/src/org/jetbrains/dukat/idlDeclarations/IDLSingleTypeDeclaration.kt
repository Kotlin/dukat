package org.jetbrains.dukat.idlDeclarations

data class IDLSingleTypeDeclaration(
        override val name: String,
        val typeParameter: IDLTypeDeclaration?,
        override val nullable: Boolean,
        override val comment: String? = null
) : IDLTypeDeclaration

fun IDLSingleTypeDeclaration.isKnown(): Boolean {
    return name in setOf(
            "ByteString",
            "DOMError",
            "DOMString",
            "String",
            "USVString",
            "\$Array",
            "\$dynamic",
            "any",
            "boolean",
            "byte",
            "double",
            "float",
            "FrozenArray",
            "long",
            "longlong",
            "object",
            "octet",
            "Promise",
            "sequence",
            "short",
            "unrestricteddouble",
            "unrestrictedfloat",
            "unsignedlong",
            "unsignedlonglong",
            "unsignedshort",
            "void"
    )
}

fun IDLSingleTypeDeclaration.isPrimitive(): Boolean {
    return name in setOf(
            "boolean",
            "byte",
            "double",
            "float",
            "long",
            "longlong",
            "octet",
            "short",
            "unrestricteddouble",
            "unrestrictedfloat",
            "unsignedlong",
            "unsignedlonglong",
            "unsignedshort"
    )
}