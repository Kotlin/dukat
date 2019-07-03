package main.org.jetbrains.dukat.idlModules

import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.idlDeclarations.IDLDeclaration
import org.jetbrains.dukat.idlDeclarations.InterfaceIDLDeclaration

fun convertToModel(declaration : IDLDeclaration) : TopLevelModel? {
    return when (declaration) {
        is InterfaceIDLDeclaration -> InterfaceModel(
                name = org.jetbrains.dukat.astCommon.IdentifierEntity(declaration.name),
                members = mutableListOf(),
                companionObject = CompanionObjectModel(
                        name = "",
                        members = mutableListOf(),
                        parentEntities = emptyList()
                ),
                typeParameters = mutableListOf(),
                parentEntities = mutableListOf(),
                annotations = mutableListOf(),
                external = false
        )
        else -> null
    }
}