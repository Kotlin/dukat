package main.org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.idlDeclarations.IDLDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLFileDeclaration
import org.jetbrains.dukat.idlDeclarations.IDLInterfaceDeclaration
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

fun IDLDeclaration.convertToModel() : TopLevelModel? {
    return when (this) {
        is IDLInterfaceDeclaration -> InterfaceModel(
                name = org.jetbrains.dukat.astCommon.IdentifierEntity(name),
                members = listOf(),
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
        else -> raiseConcern("unprocessed MemberNode: ${this}") { null }
    }
}

fun IDLFileDeclaration.process() : SourceSetModel {
    val modelDeclarations = declarations.mapNotNull { d -> d.convertToModel() }

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