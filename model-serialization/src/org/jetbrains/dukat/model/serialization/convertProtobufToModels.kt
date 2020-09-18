package org.jetbrains.dukat.model.serialization

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.InheritanceModifierModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.protobuf.kotlin.ClassModelProto
import org.jetbrains.dukat.protobuf.kotlin.HeritageModelProto
import org.jetbrains.dukat.protobuf.kotlin.InheritanceModifierModelProto
import org.jetbrains.dukat.protobuf.kotlin.InterfaceModelProto
import org.jetbrains.dukat.protobuf.kotlin.MemberModelProto
import org.jetbrains.dukat.protobuf.kotlin.ModuleModelProto
import org.jetbrains.dukat.protobuf.kotlin.ParameterModelProto
import org.jetbrains.dukat.protobuf.kotlin.SourceFileModelProto
import org.jetbrains.dukat.protobuf.kotlin.SourceSetModelProto
import org.jetbrains.dukat.protobuf.kotlin.TopLevelModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeParameterModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeValueModelProto
import org.jetbrains.dukat.tsmodelproto.IdentifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NameDeclarationProto
import org.jetbrains.dukat.tsmodelproto.QualifierDeclarationProto

fun NameDeclarationProto.convert(): NameEntity {
    return when {
        hasIdentifier() -> identifier.convert()
        hasQualifier() -> qualifier.convert()
        else -> throw Exception("unknown name proto entity ${this}")
    }
}

fun IdentifierDeclarationProto.convert(): IdentifierEntity {
    return IdentifierEntity(value)
}

fun QualifierDeclarationProto.convert(): QualifierEntity {
    val rightProto = right
    left.hasIdentifier()
    val left = if (left.hasIdentifier()) {
        IdentifierEntity(left.identifier.value)
    } else {
        left.qualifier.convert()
    }

    return QualifierEntity(left, IdentifierEntity(rightProto.value))
}

private fun TypeModelProto.convert(): TypeModel {
    return when {
        hasTypeValue() -> typeValue.convert()
        hasTypeParameterReference() -> TypeParameterReferenceModel(
            name = typeParameterReference.name.convert(),
            metaDescription = null,
            nullable = typeParameterReference.nullable
        )
        hasTypeParameter() -> typeParameter.convert()
        hasFunctionType() -> FunctionTypeModel(
            type = functionType.type.convert(),
            parameters = functionType.paramsList.map { param ->
                LambdaParameterModel(if (param.name.isEmpty()) null else param.name, param.type.convert(), true)
            },
            nullable = true,
            metaDescription = null
        )
        else -> TypeValueModel(IdentifierEntity("Any"), emptyList(), null, null)
    }
}

private fun TypeValueModelProto.convert(): TypeValueModel {
    return TypeValueModel(
            value.convert(),
            emptyList(),
            null,
            if (hasFqName()) { fqName.convert() } else null
    )
}

private fun TypeParameterModelProto.convert(): TypeParameterModel {
    return TypeParameterModel(
            type = type.convert(),
            constraints = emptyList()
    )
}

private fun ParameterModelProto.convert(): ParameterModel {
    return ParameterModel(
        name = name,
        type = type.convert(),
        vararg = false,
        initializer = null,
        modifier = null
    )
}

private fun MemberModelProto.convert(): MemberModel? {
    return when {
        hasPropertyModel() -> {
            PropertyModel(
                    name = propertyModel.name.convert(),
                    type = propertyModel.type.convert(),
                    typeParameters = emptyList(),
                    static = false,
                    override = propertyModel.overrideList.map { it.convert() },
                    immutable = propertyModel.immutable,
                    initializer = null,
                    getter = false,
                    setter = false,
                    open = propertyModel.open,
                    explicitlyDeclaredType = true,
                    lateinit = false
            )
        }
        hasMethodModel() -> {
            MethodModel(
                    name = methodModel.name.convert(),
                    parameters = methodModel.paramsList.map { it.convert() },
                    type = methodModel.type.convert(),
                    typeParameters = emptyList(),
                    static = false,
                    operator = false,
                    body = null,
                    annotations = emptyList(),
                    open = methodModel.open,
                    override = propertyModel.overrideList.map { it.convert() }
            )
        }
        else -> null
    }
}

private fun HeritageModelProto.convert(): HeritageModel {
    return HeritageModel(
        value = value.convert(),
        typeParams = typeParamsList.map { it.convert() },
        delegateTo = null
    )
}

private fun ClassModelProto.convert(): ClassModel {
    return ClassModel(
            name = name.convert(),
            members = membersList.mapNotNull { it.convert() },
            typeParameters = typeParametersList.map { it.convert() },
            companionObject = null,
            parentEntities = parentsList.map { it.convert() },
            primaryConstructor = null,
            annotations = mutableListOf(),
            comment = null,
            external = external,
            inheritanceModifier = when (inheritanceModifier.inheritance) {
                InheritanceModifierModelProto.INHERITANCE_KIND.ABSTRACT -> InheritanceModifierModel.ABSTRACT
                InheritanceModifierModelProto.INHERITANCE_KIND.OPEN -> InheritanceModifierModel.OPEN
                InheritanceModifierModelProto.INHERITANCE_KIND.SEALED -> InheritanceModifierModel.SEALED
                else -> InheritanceModifierModel.FINAL
            },
            visibilityModifier = VisibilityModifierModel.PUBLIC
    )
}

private fun InterfaceModelProto.convert(): InterfaceModel {
    return InterfaceModel(
            name = name.convert(),
            members = emptyList(),
            typeParameters = typeParametersList.map { it.convert() },
            companionObject = null,
            parentEntities = parentsList.map { it.convert() },
            annotations = mutableListOf(),
            comment = null,
            external = external,
            visibilityModifier = VisibilityModifierModel.PUBLIC
    )
}

private fun TopLevelModelProto.convert(): TopLevelModel? {
    return when {
        hasClassModel() -> classModel.convert()
        hasInterfaceModel() -> interfaceModel.convert()
        else -> null
    }
}

private fun ModuleModelProto.convert(): ModuleModel {
    return ModuleModel(
            name = name.convert(),
            shortName = IdentifierEntity(""),
            annotations = mutableListOf(),
            submodules = emptyList(),
            imports = mutableListOf(),
            declarations = declarationsList.mapNotNull { declaration ->
                declaration.convert()
            },
            comment = null
    )
}

private fun SourceFileModelProto.convert(): SourceFileModel {
    return SourceFileModel(
            name = if (hasName()) name.convert() else null,
            fileName = fileName ?: "",
            root = root.convert(),
            referencedFiles = emptyList()
    )
}

fun convertProtobufToModels(source: SourceSetModelProto): SourceSetModel {
    return SourceSetModel(
            sourceName = emptyList(),
            sources = source.sourceFilesList.map { it.convert() }
    )
}