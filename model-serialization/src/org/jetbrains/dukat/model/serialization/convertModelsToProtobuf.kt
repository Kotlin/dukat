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
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.modifiers.InheritanceModifierModel
import org.jetbrains.dukat.protobuf.kotlin.ClassModelProto
import org.jetbrains.dukat.protobuf.kotlin.FunctionTypeModelProto
import org.jetbrains.dukat.protobuf.kotlin.HeritageModelProto
import org.jetbrains.dukat.protobuf.kotlin.InheritanceModifierModelProto
import org.jetbrains.dukat.protobuf.kotlin.InterfaceModelProto
import org.jetbrains.dukat.protobuf.kotlin.MemberModelProto
import org.jetbrains.dukat.protobuf.kotlin.MethodModelProto
import org.jetbrains.dukat.protobuf.kotlin.ModuleModelProto
import org.jetbrains.dukat.protobuf.kotlin.ParameterModelProto
import org.jetbrains.dukat.protobuf.kotlin.PropertyModelProto
import org.jetbrains.dukat.protobuf.kotlin.SourceFileModelProto
import org.jetbrains.dukat.protobuf.kotlin.SourceSetModelProto
import org.jetbrains.dukat.protobuf.kotlin.TopLevelModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeParameterModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeParameterReferenceModelProto
import org.jetbrains.dukat.protobuf.kotlin.TypeValueModelProto
import org.jetbrains.dukat.tsmodelproto.IdentifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NameDeclarationProto
import org.jetbrains.dukat.tsmodelproto.QualifierDeclarationProto

private fun String.asNameEntityProto(): NameDeclarationProto {
    return NameDeclarationProto.newBuilder().setIdentifier(IdentifierDeclarationProto.newBuilder().setValue(this)).build()
}

private fun IdentifierEntity.asNameEntityProto(): NameDeclarationProto {
    return value.asNameEntityProto()
}

private fun QualifierEntity.asNameEntityProto(): NameDeclarationProto {
    return NameDeclarationProto.newBuilder().setQualifier(
            QualifierDeclarationProto.newBuilder()
                    .setLeft(left.asNameEntityProto())
                    .setRight(IdentifierDeclarationProto.newBuilder().setValue(right.value))
    ).build()
}

private fun NameEntity.asNameEntityProto(): NameDeclarationProto {
    return when (this) {
        is IdentifierEntity -> asNameEntityProto()
        is QualifierEntity -> asNameEntityProto()
    }
}

private fun TypeModel.convert(): TypeModelProto {
    val typeModelBuilder = TypeModelProto.newBuilder()
    when (this) {
        is TypeValueModel -> typeModelBuilder.setTypeValue(
                TypeValueModelProto
                        .newBuilder().setValue(value.asNameEntityProto())
        )
        is TypeParameterReferenceModel -> typeModelBuilder.setTypeParameterReference(
                TypeParameterReferenceModelProto.newBuilder()
                        .setName(name.asNameEntityProto())
                        .setNullable(nullable)
        )
        is TypeParameterModel -> typeModelBuilder.typeParameter = convertAsTypeParameterModel()
        is FunctionTypeModel -> typeModelBuilder.setFunctionType(
                FunctionTypeModelProto.newBuilder()
                        .setType(
                                type.convert()
                        )
                        .setNullable(nullable)
                        .addAllParams(parameters.map { parameter -> parameter.convert() })
        )
    }
    return typeModelBuilder.build()
}

private fun TypeParameterModel.convertAsTypeParameterModel(): TypeParameterModelProto {
    return TypeParameterModelProto.newBuilder().setType(
            type.convert()
    ).build()
}

private fun TypeValueModel.convertAsTypeValue(): TypeValueModelProto {
    return TypeValueModelProto.newBuilder()
        .setValue(value.asNameEntityProto())
            .addAllParams(params.map { it.convertAsTypeParameterModel() })
        .build()
}

private fun LambdaParameterModel.convert(): ParameterModelProto {
    val parameterModelBuilder = ParameterModelProto
            .newBuilder()
            .setType(type.convert())

    if (name != null) {
        parameterModelBuilder.name = name
    }

    return parameterModelBuilder.build()
}

private fun ParameterModel.convert(): ParameterModelProto {
    return ParameterModelProto
            .newBuilder()
            .setName(name)
            .setType(type.convert())
            .build()
}

private fun MemberModel.convert(): MemberModelProto? {
    val memberModeBuilder = MemberModelProto.newBuilder()
    return when (this) {
        is PropertyModel -> {
            val propertyModel = PropertyModelProto.newBuilder()
                    .setName(name.asNameEntityProto())
                    .setImmutable(immutable)
                    .setType(type.convert())
                    .setOpen(open)

            override?.let {
                propertyModel.addAllOverride(it.map { it.asNameEntityProto() })
            }

            memberModeBuilder.setPropertyModel(propertyModel).build()
        }
        is MethodModel -> {
            val methodBuilder = MethodModelProto.newBuilder()
                    .setName(name.asNameEntityProto())
                    .setType(type.convert())
                    .setOpen(open)
                    .addAllParams(
                            parameters.map { parameter -> parameter.convert() }
                    )

            override?.let {
                methodBuilder.addAllOverride(it.map { it.asNameEntityProto() })
            }

            memberModeBuilder.setMethodModel(methodBuilder).build()
        }
        else -> null
    }
}

private fun HeritageModel.convert(): HeritageModelProto {
    val heritageModelBuilder = HeritageModelProto.newBuilder()
    heritageModelBuilder.value = value.convertAsTypeValue()
    heritageModelBuilder.addAllTypeParams(typeParams.map { it.convert() })
    return heritageModelBuilder.build()
}

private fun ClassModel.convertAsClass(): ClassModelProto {
    val classModelBuilder = ClassModelProto.newBuilder()
    classModelBuilder.name = name.asNameEntityProto()

    classModelBuilder.addAllTypeParameters(typeParameters.map { it.convertAsTypeParameterModel() })
    classModelBuilder.addAllMembers(members.mapNotNull { it.convert() })
    classModelBuilder.addAllParents(parentEntities.map { it.convert() })

    when (inheritanceModifier) {
        InheritanceModifierModel.ABSTRACT -> classModelBuilder.setInheritanceModifier(InheritanceModifierModelProto.newBuilder().setInheritance(InheritanceModifierModelProto.INHERITANCE_KIND.ABSTRACT))
        InheritanceModifierModel.OPEN -> classModelBuilder.setInheritanceModifier(InheritanceModifierModelProto.newBuilder().setInheritance(InheritanceModifierModelProto.INHERITANCE_KIND.OPEN))
        InheritanceModifierModel.SEALED -> classModelBuilder.setInheritanceModifier(InheritanceModifierModelProto.newBuilder().setInheritance(InheritanceModifierModelProto.INHERITANCE_KIND.SEALED))
        else -> classModelBuilder.setInheritanceModifier(InheritanceModifierModelProto.newBuilder().setInheritance(InheritanceModifierModelProto.INHERITANCE_KIND.FINAL))
    }

    classModelBuilder.external = external
    return classModelBuilder.build()
}

private fun InterfaceModel.convertAsInterface(): InterfaceModelProto {
    val interfaceModelBuilder = InterfaceModelProto.newBuilder()
    interfaceModelBuilder.name = (name as IdentifierEntity).value.asNameEntityProto()

    interfaceModelBuilder.addAllTypeParameters(typeParameters.map { it.convertAsTypeParameterModel() })
    interfaceModelBuilder.addAllMembers(members.mapNotNull { it.convert() })
    interfaceModelBuilder.addAllParents(parentEntities.map { it.convert() })

    interfaceModelBuilder.external = external
    return interfaceModelBuilder.build()
}

private fun TopLevelModel.convert(): TopLevelModelProto? {
    val topLevelModelBuilder = TopLevelModelProto.newBuilder()
    return when (this) {
        is VariableModel -> {
            null
        }
        is ClassModel -> {
            topLevelModelBuilder.setClassModel(convertAsClass()).build()
        }
        is InterfaceModel -> {
            topLevelModelBuilder.setInterfaceModel(convertAsInterface()).build()
        }
        else -> {
            println("SKIPPING ${name} ${this::class.java}")
            null
        }
    }
}

private fun ModuleModel.convert(): ModuleModelProto {
    val moduleBuilder = ModuleModelProto.newBuilder()

    moduleBuilder.addAllDeclarations(declarations.mapNotNull { topLevelModel ->
        topLevelModel.convert()
    })

    moduleBuilder.name = name.asNameEntityProto()

    return moduleBuilder.build()
}

private fun SourceFileModel.convert(): SourceFileModelProto {
    val sourceFileBuilder = SourceFileModelProto.newBuilder()
    sourceFileBuilder.root = root.convert()
    sourceFileBuilder.fileName = fileName

    name?.asNameEntityProto()?.let {
        sourceFileBuilder.name = it
    }
    return sourceFileBuilder.build()
}

fun convertModelsToProtoBuf(source: SourceSetModel): SourceSetModelProto {
    val sourceSetBuilder = SourceSetModelProto.newBuilder()

    sourceSetBuilder.addAllSourceFiles(source.sources.map { it.convert() })
    return sourceSetBuilder.build()
}