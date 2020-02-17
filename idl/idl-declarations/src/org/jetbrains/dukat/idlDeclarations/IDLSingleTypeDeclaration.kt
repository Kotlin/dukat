package org.jetbrains.dukat.idlDeclarations

data class IDLSingleTypeDeclaration(
        override val name: String,
        val typeParameter: IDLTypeDeclaration?,
        override val nullable: Boolean,
        override val comment: String? = null
) : IDLTypeDeclaration

fun IDLSingleTypeDeclaration.isKnown(): Boolean {
    return name in setOf(
            "ArrayBuffer",
            "ArrayBufferView",
            "BufferDataSource",
            "ByteString",
            "CSSOMString",
            "DataView",
            "DOMError",
            "DOMString",
            "Float32Array",
            "Float64Array",
            "Int8Array",
            "Int16Array",
            "Int32Array",
            "String",
            "Uint8Array",
            "Uint8ClampedArray",
            "Uint16Array",
            "Uint32Array",
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
            "record",
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