package org.jetbrains.dukat.idlModels

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.astModel.CompanionObjectModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.idlDeclarations.*
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import java.io.File

fun IDLTypeDeclaration.process(): TypeValueModel {
    val typeModel = TypeValueModel(
            value = IdentifierEntity(when (name) {
                "void" -> "Unit"
                "float" -> "Float"
                "unrestrictedfloat" -> "Float"
                "double" -> "Double"
                "unrestricteddouble" -> "Double"
                "long" -> "Int"
                "unsignedlong" -> "Int"
                "longlong" -> "Int"
                "unsignedlonglong" -> "Int"
                "octet" -> "Byte"
                "byte" -> "Byte"
                "short" -> "Short"
                "unsignedshort" -> "Short"
                "boolean" -> "Boolean"
                "ByteString" -> "String"
                "DOMString" -> "String"
                "USVString" -> "String"
                "\$Array" -> "Array"
                "sequence" -> "Array"
                "object" -> "dynamic"
                "DOMError" -> "dynamic"
                else -> name
            }),
            params = listOfNotNull(typeParameter?.process()),
            metaDescription = null
    )
    return typeModel.copy(
            nullable = if (typeModel.value == IdentifierEntity("dynamic")) {
                false
            } else {
                nullable
            }
    )
}

fun IDLArgumentDeclaration.process(): ParameterModel {
    return ParameterModel(
            name = name,
            type = type.process(),
            initializer = null,
            vararg = false,
            optional = false
    )
}

fun IDLInterfaceDeclaration.convertToModel(): TopLevelModel {
    return if (extendedAttributes.contains(IDLSimpleExtendedAttributeDeclaration("NoInterfaceObject"))) {
        InterfaceModel(
                name = IdentifierEntity(name),
                members = attributes.filterNot { it.static }.mapNotNull { it.process() } +
                        operations.filterNot { it.static }.mapNotNull { it.process() },
                companionObject = CompanionObjectModel(
                        name = "",
                        members = constants.mapNotNull { it.process() } +
                                operations.filter { it.static }.mapNotNull { it.process() } +
                                attributes.filter { it.static }.mapNotNull { it.process() },
                        parentEntities = listOf()
                ),
                typeParameters = listOf(),
                parentEntities = parents.map {
                    HeritageModel(
                            it.process(),
                            listOf(),
                            null
                    )
                },
                annotations = mutableListOf(),
                external = true
        )
    } else {
        ClassModel(
                name = IdentifierEntity(name),
                members = attributes.filterNot { it.static }.mapNotNull { it.process() } +
                        operations.filterNot { it.static }.mapNotNull { it.process() } +
                        constructors.mapNotNull { it.process() },
                companionObject = CompanionObjectModel(
                        name = "",
                        members = constants.mapNotNull { it.process() } +
                                operations.filter { it.static }.mapNotNull { it.process() } +
                                attributes.filter { it.static }.mapNotNull { it.process() },
                        parentEntities = listOf()
                ),
                typeParameters = listOf(),
                parentEntities = parents.map {
                    HeritageModel(
                            it.process(),
                            listOf(),
                            null
                    )
                },
                primaryConstructor = if (primaryConstructor != null) {
                    primaryConstructor!!.process() as ConstructorModel
                } else {
                    null
                },
                annotations = mutableListOf(),
                external = true,
                abstract = constructors.isEmpty() && primaryConstructor == null
        )
    }
}

fun IDLTopLevelDeclaration.convertToModel(): TopLevelModel? {
    return when (this) {
        is IDLInterfaceDeclaration -> convertToModel()
        is IDLTypedefDeclaration -> null
        else -> raiseConcern("unprocessed top level declaration: ${this}") { null }
    }
}

fun IDLMemberDeclaration.process(): MemberModel? {
    return when (this) {
        is IDLAttributeDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                getter = true,
                setter = !readOnly,
                open = false
        )
        is IDLOperationDeclaration -> MethodModel(
                name = IdentifierEntity(name),
                parameters = arguments.map { it.process() },
                type = returnType.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                operator = false,
                annotations = listOf(),
                open = false
        )
        is IDLConstructorDeclaration -> ConstructorModel(
                parameters = arguments.map { it.process() },
                typeParameters = listOf(),
                generated = false
        )
        is IDLConstantDeclaration -> PropertyModel(
                name = IdentifierEntity(name),
                type = type.process(),
                typeParameters = listOf(),
                static = false,
                override = false,
                getter = true,
                setter = false,
                open = false
        )
        else -> raiseConcern("unprocessed member declaration: ${this}") { null }
    }
}

fun IDLFileDeclaration.process(): SourceSetModel {
    val modelDeclarations = declarations.mapNotNull { it.convertToModel() }

    val module = ModuleModel(
            name = ROOT_PACKAGENAME,
            shortName = ROOT_PACKAGENAME,
            declarations = modelDeclarations,
            annotations = mutableListOf(),
            submodules = listOf(),
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