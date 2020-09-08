package org.jetbrains.dukat.model.serialization

import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.protobuf.kotlin.SourceSetModelProto
import java.io.File

fun readSourceSetFromFile(path: String): SourceSetModel {
    return convertProtobufToModels(SourceSetModelProto.parseFrom(File(path).readBytes()))
}