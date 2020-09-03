package org.jetbrains.dukat.model.serialization

import org.jetbrains.dukat.protobuf.kotlin.SourceSetModelProto
import org.jetbrains.dukat.stdlibGenerator.createKotlinStdLibFromMetaDescriptors
import java.io.File

fun serializeStdLib(path: String): SourceSetModelProto {
    val sourceSet = createKotlinStdLibFromMetaDescriptors(path)
    return convertModelsToProtoBuf(sourceSet)
}

fun main(vararg args: String) {
    println(args.joinToString(" :: "))
    val binary = serializeStdLib(args[0])
    File(args[1]).writeBytes(binary.toByteArray())
}