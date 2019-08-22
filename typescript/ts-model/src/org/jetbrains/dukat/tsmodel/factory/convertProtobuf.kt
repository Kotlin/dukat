package org.jetbrains.dukat.tsmodel.factory

import dukat.ast.proto.Declarations
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
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
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
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
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

fun Declarations.NameEntityProto.convert(): NameEntity {
    return when {
        hasIdentifier() -> identifier.convert()
        hasQualifier() -> qualifier.convert()
        else -> throw Exception("unknown name proto entity ${this}")
    }
}

fun Declarations.IdentifierEntityProto.convert(): IdentifierEntity {
    return IdentifierEntity(value)
}

fun Declarations.QualifierEntityProto.convert(): QualifierEntity {
    val rightProto = right
    left.hasIdentifier()
    val left = if (left.hasIdentifier()) {
        IdentifierEntity(left.identifier.value)
    } else {
        left.qualifier.convert()
    }

    return QualifierEntity(left, IdentifierEntity(rightProto.value))
}

fun Declarations.ModifierDeclarationProto.convert(): ModifierDeclaration {
    return ModifierDeclaration(token)
}

fun Declarations.HeritageClauseDeclarationProto.convert(): HeritageClauseDeclaration {
    return HeritageClauseDeclaration(
        name.convert(),
        typeArgumentsList.map { it.convert() },
        extending,
        if (hasTypeReference()) ReferenceEntity(typeReference.uid) else null
    )
}

fun Declarations.ClassDeclarationProto.convert(): ClassDeclaration {
    return ClassDeclaration(
            name.convert(),
            membersList.map { it.convert() },
            typeParametersList.map { it.convert() },
            parentEntitiesList.map { it.convert() },
            modifiersList.map { it.convert() },
            uid
    )
}

fun Declarations.InterfaceDeclarationProto.convert(): InterfaceDeclaration {
    return InterfaceDeclaration(
            name.convert(),
            membersList.map { it.convert() },
            typeParametersList.map { it.convert() },
            parentEntitiesList.map { it.convert() },
            definitionsInfoList.map { DefinitionInfoDeclaration(it.fileName) } ,
            uid
    )
}

fun Declarations.FunctionDeclarationProto.convert(): FunctionDeclaration {
    return FunctionDeclaration(
        name,
        parametersList.map { it.convert() },
        type.convert(),
        typeParametersList.map { it.convert() },
        modifiersList.map { it.convert() },
        uid
    )
}

fun Declarations.TypeAliasDeclarationProto.convert(): TypeAliasDeclaration {
    return TypeAliasDeclaration(
        aliasName.convert(),
        typeParametersList.map { it.convert() },
        typeReference.convert(),
        uid
    )
}

fun Declarations.VariableDeclarationProto.convert(): VariableDeclaration {
    return VariableDeclaration(name, type.convert(), modifiersList.map { it.convert() }, uid)
}

fun Declarations.EnumDeclaration.convert(): EnumDeclaration {
    return EnumDeclaration(name, valuesList.map { EnumTokenDeclaration(it.value, it.meta) })
}

private fun Declarations.DefinitionInfoDeclarationProto.convert(): DefinitionInfoDeclaration {
    return DefinitionInfoDeclaration(fileName)
}

fun Declarations.ModuleDeclarationProto.convert(): ModuleDeclaration {
    return ModuleDeclaration(packageName.convert(),
            declarationsList.map { it.convert() },
            modifiersList.map { it.convert() },
            definitionsInfoList.map { it.convert() },
            uid,
            resourceName,
            root)
}

fun Declarations.ExportAssignmentDeclarationProto.convert(): ExportAssignmentDeclaration {
    return ExportAssignmentDeclaration(name, isExportEquals)
}

fun Declarations.ImportEqualsDeclarationProto.convert(): ImportEqualsDeclaration {
    return ImportEqualsDeclaration(name, moduleReference.convert(), uid)
}

fun Declarations.TopLevelEntityProto.convert(): TopLevelDeclaration {
    return if (hasClassDeclaration()) {
        classDeclaration.convert()
    } else if (hasInterfaceDeclaration()) {
        interfaceDeclaration.convert()
    } else if (hasFunctionDeclaration()) {
        functionDeclaration.convert()
    } else if (hasAliasDeclaration()) {
        aliasDeclaration.convert()
    } else if (hasVariableDeclaration()) {
        variableDeclaration.convert()
    } else if (hasEnumDeclaration()) {
        enumDeclaration.convert()
    } else if (hasModuleDeclaration()) {
        moduleDeclaration.convert()
    } else if (hasExportAssignment()) {
        exportAssignment.convert()
    } else if (hasImportEquals()) {
        importEquals.convert()
    } else {
        throw Exception("unknown TopLevelEntity: ${this}")
    }
}

fun Declarations.PropertyDeclarationProto.convert(): PropertyDeclaration {
    return PropertyDeclaration(
        name,
        type.convert(),
        typeParametersList.map { it.convert() },
        optional,
        modifiersList.map { it.convert() }
    )
}

fun Declarations.IndexSignatureDeclarationProto.convert(): IndexSignatureDeclaration {
    return IndexSignatureDeclaration(
        indexTypesList.map { it.convert() },
        returnType.convert()
    )
}

fun Declarations.CallSignatureDeclarationProto.convert(): CallSignatureDeclaration {
    return CallSignatureDeclaration(
            parametersList.map { it.convert() },
            type.convert(),
            typeParametersList.map { it.convert() }
    )
}

fun Declarations.MemberEntityProto.convert(): MemberEntity {
    return if (hasConstructorDeclaration()) {
        constructorDeclaration.convert()
    } else if (hasMethodSignature()) {
        methodSignature.convert()
    } else if (hasFunctionDeclaration()) {
        functionDeclaration.convert()
    } else if (hasProperty()) {
        property.convert()
    } else if (hasIndexSignature()) {
        indexSignature.convert()
    } else if (hasCallSignature()) {
        callSignature.convert()
    } else {
        throw Exception("unknown MemberEntityProto: ${this}")
    }
}

fun Declarations.ConstructorDeclarationProto.convert(): ConstructorDeclaration {
    return ConstructorDeclaration(
        parametersList.map { it.convert() },
        typeParametersList.map { it.convert() },
        modifiersList.map { it.convert() }
    )
}

fun Declarations.MethodSignatureDeclarationProto.convert(): MethodSignatureDeclaration {
    return MethodSignatureDeclaration(
            name,
            parametersList.map { it.convert() },
            type.convert(),
            typeParametersList.map { it.convert() },
            optional,
            modifiersList.map { it.convert() }
    )
}

private fun Declarations.TypeParameterDeclarationProto.convert(): TypeParameterDeclaration {
    return TypeParameterDeclaration(name.convert(), constraintsList.map { constraintProto -> constraintProto.convert() })
}


private fun Declarations.TypeDeclarationProto.convert(): TypeDeclaration {
    return TypeDeclaration(value.convert(), paramsList.map { it.convert() }, ReferenceEntity(typeReference.uid))
}

private fun Declarations.ParameterDeclarationProto.convert(): ParameterDeclaration {
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

private fun Declarations.ParameterValueDeclarationProto.convert(): ParameterValueDeclaration {
    return if (hasStringLiteral()) {
        StringLiteralDeclaration(stringLiteral.token)
    } else if (hasThisType()) {
        ThisTypeDeclaration()
    } else if (hasGeneratedInterfaceReference()) {
        GeneratedInterfaceReferenceDeclaration(generatedInterfaceReference.name.convert(), generatedInterfaceReference.typeParametersList.map { it.convert() })
    } else if (hasIntersectionType()) {
        IntersectionTypeDeclaration(intersectionType.paramsList.map { it.convert() })
    } else if (hasTupleDeclaration()) {
        TupleDeclaration(tupleDeclaration.paramsList.map { it.convert() })
    } else if (hasUnionType()) {
        UnionTypeDeclaration(unionType.paramsList.map { it.convert() })
    } else if (hasTypeDeclaration()) {
        with(typeDeclaration) {
            TypeDeclaration(
                    value.convert(),
                    paramsList.map { it.convert() },
                    ReferenceEntity(typeReference.uid)
            )
        }
    } else if (hasObjectLiteral()) {
        val objectLiteral = objectLiteral
        ObjectLiteralDeclaration(
                objectLiteral.membersList.map { it.convert() }
        )
    } else if (hasFunctionTypeDeclaration()) {
        with(functionTypeDeclaration) {
            FunctionTypeDeclaration(
                    parametersList.map { it.convert() },
                    type.convert()
            )
        }
    } else {
        throw Exception("unknown ParameterValueDeclarationProto ${this}")
    }
}

fun Declarations.SourceFileDeclarationProto.convert(): SourceFileDeclaration {
    return SourceFileDeclaration(
            fileName,
            root.convert(),
            referencedFilesList.map { IdentifierEntity(it.value) }
    )
}

fun Declarations.SourceSetDeclarationProto.convert(): SourceSetDeclaration {
    return SourceSetDeclaration(sourceName, sourcesList.map { it.convert() })
}

fun Declarations.SourceSetBundleProto.convert(): SourceBundleDeclaration {
    return SourceBundleDeclaration(sourcesList.map { it.convert() })
}