package main.org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.idlDeclarations.IDLAttributeDeclaration
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.idlDeclarations.IDLDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

fun convertPrimitiveType(type: String): String {
    return when (type) {
        "float" -> "Float"
        "double" -> "Double"
        "long" -> "Int"
        "byte" -> "Byte"
        "short" -> "Short"
        "longlong" -> "Long"
        else -> "Any"
    }
}

fun IDLDeclaration.convertToModel() : MemberModel? {
    return when (this) {
        is IDLInterfaceDeclaration -> InterfaceModel(
                name = IdentifierEntity(name),
                members = attributes.mapNotNull { d -> d.convertToModel() },
                companionObject = CompanionObjectModel(
                        name = "",
                        members = listOf(),
                        parentEntities = listOf()
                ),
                typeParameters = listOf(),
                parentEntities = listOf(),
                annotations = mutableListOf(),
                external = false
        )
        is IDLAttributeDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = TypeValueModel(
                        value = IdentifierEntity(convertPrimitiveType(type.name)),
                        params = listOf(),
                        metaDescription = null
                ),
                typeParameters = listOf(),
                static = false,
                override = false,
                getter = false,
                setter = false,
                open = false
        )
        else -> raiseConcern("unprocessed MemberNode: ${this}") { null }
    }
}

fun IDLFileDeclaration.process() : SourceSetModel {
    val modelDeclarations = declarations.mapNotNull { d -> d.convertToModel() }.map { d -> d as TopLevelModel }

    val module = ModuleModel(
            name = ROOT_PACKAGENAME,
            shortName = ROOT_PACKAGENAME,
            declarations = modelDeclarations,
            annotations = mutableListOf(),
            sumbodules = listOf(),
            imports = mutableListOf()
    )

    val source = SourceFileModel(
            name = null,
            fileName = File(fileName).normalize().absolutePath,
            root = module,
            referencedFiles = listOf()
    )

    return SourceSetModel(listOf(source))
}