package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.EnumTokenDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodelproto.CallSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ClassDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ConstructorDeclarationProto
import org.jetbrains.dukat.tsmodelproto.DefinitionInfoDeclarationProto
import org.jetbrains.dukat.tsmodelproto.EnumDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExportAssignmentDeclarationProto
import org.jetbrains.dukat.tsmodelproto.FunctionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.HeritageClauseDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IdentifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ImportEqualsDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IndexSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.InterfaceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.MemberDeclarationProto
import org.jetbrains.dukat.tsmodelproto.MethodSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ModifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ModuleDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NameDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParameterDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParameterValueDeclarationProto
import org.jetbrains.dukat.tsmodelproto.PropertyDeclarationProto
import org.jetbrains.dukat.tsmodelproto.QualifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ReferenceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceBundleDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceFileDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceSetDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TopLevelDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeAliasDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeParameterDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeReferenceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.VariableDeclarationProto

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

fun ModifierDeclarationProto.convert(): ModifierDeclaration {
    return ModifierDeclaration(token)
}

fun <T : Entity> ReferenceDeclarationProto.convert(): ReferenceEntity<T> {
    return ReferenceEntity(uid)
}

fun HeritageClauseDeclarationProto.convert(): HeritageClauseDeclaration {
    return HeritageClauseDeclaration(
            name.convert(),
            typeArgumentsList.map { it.convert() },
            extending,
            if (hasTypeReference()) typeReference.convert() else null
    )
}

fun ClassDeclarationProto.convert(): ClassDeclaration {
    return ClassDeclaration(
            name.convert(),
            membersList.map { it.convert() },
            typeParametersList.map { it.convert() },
            parentEntitiesList.map { it.convert() },
            modifiersList.map { it.convert() },
            uid
    )
}

fun InterfaceDeclarationProto.convert(): InterfaceDeclaration {
    return InterfaceDeclaration(
            name.convert(),
            membersList.map { it.convert() },
            typeParametersList.map { it.convert() },
            parentEntitiesList.map { it.convert() },
            definitionsInfoList.map { DefinitionInfoDeclaration(it.fileName) },
            uid
    )
}

fun FunctionDeclarationProto.convert(): FunctionDeclaration {
    return FunctionDeclaration(
            name,
            parametersList.map { it.convert() },
            type.convert(),
            typeParametersList.map { it.convert() },
            modifiersList.map { it.convert() },
            uid
    )
}

fun TypeAliasDeclarationProto.convert(): TypeAliasDeclaration {
    return TypeAliasDeclaration(
            aliasName.convert(),
            typeParametersList.map { it.convert() },
            typeReference.convert(),
            uid
    )
}

fun VariableDeclarationProto.convert(): VariableDeclaration {
    return VariableDeclaration(name, type.convert(), modifiersList.map { it.convert() }, uid)
}

fun EnumDeclarationProto.convert(): EnumDeclaration {
    return EnumDeclaration(name, valuesList.map { EnumTokenDeclaration(it.value, it.meta) }, uid)
}

private fun DefinitionInfoDeclarationProto.convert(): DefinitionInfoDeclaration {
    return DefinitionInfoDeclaration(fileName)
}

fun ModuleDeclarationProto.convert(): ModuleDeclaration {
    return ModuleDeclaration(packageName.convert(),
            declarationsList.map { it.convert() },
            modifiersList.map { it.convert() },
            definitionsInfoList.map { it.convert() },
            uid,
            resourceName,
            root)
}

fun ExportAssignmentDeclarationProto.convert(): ExportAssignmentDeclaration {
    return ExportAssignmentDeclaration(name, isExportEquals)
}

fun ImportEqualsDeclarationProto.convert(): ImportEqualsDeclaration {
    return ImportEqualsDeclaration(name, moduleReference.convert(), uid)
}

fun TopLevelDeclarationProto.convert(): TopLevelDeclaration {
    return when {
        hasClassDeclaration() -> classDeclaration.convert()
        hasInterfaceDeclaration() -> interfaceDeclaration.convert()
        hasFunctionDeclaration() -> functionDeclaration.convert()
        hasAliasDeclaration() -> aliasDeclaration.convert()
        hasVariableDeclaration() -> variableDeclaration.convert()
        hasEnumDeclaration() -> enumDeclaration.convert()
        hasModuleDeclaration() -> moduleDeclaration.convert()
        hasExportAssignment() -> exportAssignment.convert()
        hasImportEquals() -> importEquals.convert()
        else -> throw Exception("unknown TopLevelEntity: ${this}")
    }
}

fun PropertyDeclarationProto.convert(): PropertyDeclaration {
    return PropertyDeclaration(
            name,
            type.convert(),
            typeParametersList.map { it.convert() },
            optional,
            modifiersList.map { it.convert() }
    )
}

fun IndexSignatureDeclarationProto.convert(): IndexSignatureDeclaration {
    return IndexSignatureDeclaration(
            indexTypesList.map { it.convert() },
            returnType.convert()
    )
}

fun CallSignatureDeclarationProto.convert(): CallSignatureDeclaration {
    return CallSignatureDeclaration(
            parametersList.map { it.convert() },
            type.convert(),
            typeParametersList.map { it.convert() }
    )
}

fun MemberDeclarationProto.convert(): MemberDeclaration {
    return when {
        hasConstructorDeclaration() -> constructorDeclaration.convert()
        hasMethodSignature() -> methodSignature.convert()
        hasFunctionDeclaration() -> functionDeclaration.convert()
        hasProperty() -> property.convert()
        hasIndexSignature() -> indexSignature.convert()
        hasCallSignature() -> callSignature.convert()
        else -> throw Exception("unknown MemberEntityProto: ${this}")
    }
}

fun ConstructorDeclarationProto.convert(): ConstructorDeclaration {
    return ConstructorDeclaration(
            parametersList.map { it.convert() },
            typeParametersList.map { it.convert() },
            modifiersList.map { it.convert() }
    )
}

fun MethodSignatureDeclarationProto.convert(): MethodSignatureDeclaration {
    return MethodSignatureDeclaration(
            name,
            parametersList.map { it.convert() },
            type.convert(),
            typeParametersList.map { it.convert() },
            optional,
            modifiersList.map { it.convert() }
    )
}

private fun TypeParameterDeclarationProto.convert(): TypeParameterDeclaration {
    return TypeParameterDeclaration(
            name.convert(),
            constraintsList.map { constraintProto -> constraintProto.convert() },
            if (hasDefaultValue()) {
                defaultValue.convert()
            } else {
                null
            }
    )
}


private fun TypeReferenceDeclarationProto.convert(): TypeDeclaration {
    return TypeDeclaration(
            value.convert(),
            paramsList.map { it.convert() },
            if (hasTypeReference()) {
                typeReference.convert()
            } else null
    )
}

private fun ParameterDeclarationProto.convert(): ParameterDeclaration {
    return ParameterDeclaration(
            name,
            type.convert(),
            if (hasInitializer()) {
                ExpressionDeclaration(initializer.kind.convert(), initializer.meta)
            } else null,
            vararg,
            optional
    )
}

private fun ParameterValueDeclarationProto.convert(): ParameterValueDeclaration {
    return when {
        hasStringLiteral() -> StringLiteralDeclaration(stringLiteral.token)
        hasThisType() -> ThisTypeDeclaration()
        hasIntersectionType() -> IntersectionTypeDeclaration(intersectionType.paramsList.map { it.convert() })
        hasTupleDeclaration() -> TupleDeclaration(tupleDeclaration.paramsList.map { it.convert() })
        hasUnionType() -> UnionTypeDeclaration(unionType.paramsList.map { it.convert() })
        hasTypeReferenceDeclaration() -> with(typeReferenceDeclaration) {
            TypeDeclaration(
                    value.convert(),
                    paramsList.map { it.convert() },
                    if (typeReferenceDeclaration.hasTypeReference()) typeReference.convert() else null
            )
        }
        hasTypeParamReferenceDeclaration() -> with(typeParamReferenceDeclaration) {
            TypeParamReferenceDeclaration(value.convert())
        }
        hasObjectLiteral() -> {
            val objectLiteral = objectLiteral
            ObjectLiteralDeclaration(
                    objectLiteral.membersList.map { it.convert() },
                    objectLiteral.uid
            )
        }
        hasFunctionTypeDeclaration() -> with(functionTypeDeclaration) {
            FunctionTypeDeclaration(
                    parametersList.map { it.convert() },
                    type.convert()
            )
        }
        else -> throw Exception("unknown ParameterValueDeclarationProto ${this}")
    }
}

fun SourceFileDeclarationProto.convert(): SourceFileDeclaration {
    return SourceFileDeclaration(
            fileName,
            root.convert(),
            referencedFilesList
    )
}

fun SourceSetDeclarationProto.convert(): SourceSetDeclaration {
    return SourceSetDeclaration(sourceName, sourcesList.map { it.convert() })
}

fun SourceBundleDeclarationProto.convert(): SourceBundleDeclaration {
    return SourceBundleDeclaration(sourcesList.map { it.convert() })
}