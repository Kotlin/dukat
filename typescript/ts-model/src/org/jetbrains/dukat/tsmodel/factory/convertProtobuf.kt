package org.jetbrains.dukat.tsmodel.factory

import dukat.ast.proto.Declarations
import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.EnumTokenDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceBundleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ObjectLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.RegExLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.NameExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
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

fun <T : Entity> Declarations.ReferenceEntityProto.convert(): ReferenceEntity<T> {
    return ReferenceEntity(uid)
}

fun Declarations.HeritageClauseDeclarationProto.convert(): HeritageClauseDeclaration {
    return HeritageClauseDeclaration(
            name.convert(),
            typeArgumentsList.map { it.convert() },
            extending,
            if (hasTypeReference()) typeReference.convert() else null
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
            definitionsInfoList.map { DefinitionInfoDeclaration(it.fileName) },
            uid
    )
}

fun Declarations.BlockDeclarationProto.convert() : BlockDeclaration {
    return BlockDeclaration(
            statements = statementsList.map { it.convert() }
    )
}

fun Declarations.FunctionDeclarationProto.convert(): FunctionDeclaration {
    return FunctionDeclaration(
            name,
            parametersList.map { it.convert() },
            type.convert(),
            typeParametersList.map { it.convert() },
            modifiersList.map { it.convert() },
            if (hasBody()) {
                body.convert()
            } else null,
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
    return VariableDeclaration(
            name,
            type.convert(),
            modifiersList.map { it.convert() },
            if (hasInitializer()) {
                initializer?.convert()
            } else null,
            uid
    )
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

fun List<Declarations.TopLevelEntityProto>.convert(): TopLevelDeclaration? {
    return when {
        this.isEmpty() -> null
        this.size == 1 -> this[0].convert()
        else -> BlockDeclaration( this.map { it.convert() } )
    }
}

fun Declarations.IfStatementDeclarationProto.convert(): IfStatementDeclaration {
    return IfStatementDeclaration(
            condition = condition.convert(),
            thenStatement = thenStatementList.convert() ?: BlockDeclaration(emptyList()),
            elseStatement = elseStatementList.convert()
    )
}

fun Declarations.ExpressionStatementDeclarationProto.convert(): ExpressionStatementDeclaration {
    return ExpressionStatementDeclaration(expression.convert())
}

fun Declarations.ReturnStatementDeclarationProto.convert(): ReturnStatementDeclaration {
    return ReturnStatementDeclaration(
            if(hasExpression()) {
                expression.convert()
            } else null
    )
}

fun Declarations.TopLevelEntityProto.convert(): TopLevelDeclaration {
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
        hasIfStatement() -> ifStatement.convert()
        hasExpressionStatement() -> expressionStatement.convert()
        hasReturnStatement() -> returnStatement.convert()
        hasBlockStatement() -> blockStatement.convert()
        else -> throw Exception("unknown TopLevelEntity: ${this}")
    }
}

fun Declarations.PropertyDeclarationProto.convert(): PropertyDeclaration {
    return PropertyDeclaration(
            name,
            if (hasInitializer()) {
                initializer.convert()
            } else null,
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

fun Declarations.MemberEntityProto.convert(): MemberDeclaration {
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


private fun Declarations.TypeReferenceDeclarationProto.convert(): TypeDeclaration {
    return TypeDeclaration(
            value.convert(),
            paramsList.map { it.convert() },
            if (hasTypeReference()) {
                typeReference.convert()
            } else null
    )
}

private fun Declarations.ParameterDeclarationProto.convert(): ParameterDeclaration {
    return ParameterDeclaration(
            name,
            type.convert(),
            if (hasInitializer()) {
                initializer.convert()
            } else null,
            vararg,
            optional
    )
}

private fun Declarations.ParameterValueDeclarationProto.convert(): ParameterValueDeclaration {
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
                    objectLiteral.membersList.map { it.convert() }
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


fun Declarations.BinaryExpressionDeclarationProto.convert() : BinaryExpressionDeclaration {
    return BinaryExpressionDeclaration(
            left = left.convert(),
            operator = operator,
            right = right.convert()
    )
}

fun Declarations.UnaryExpressionDeclarationProto.convert() : UnaryExpressionDeclaration {
    return UnaryExpressionDeclaration(
            operand = operand.convert(),
            operator = operator,
            isPrefix = isPrefix
    )
}

fun Declarations.TypeOfExpressionDeclarationProto.convert(): TypeOfExpressionDeclaration {
    return TypeOfExpressionDeclaration(
            expression = expression.convert()
    )
}

fun Declarations.CallExpressionDeclarationProto.convert(): CallExpressionDeclaration {
    return CallExpressionDeclaration(
            expression = expression.convert(),
            arguments = argumentsList.map { it.convert() }
    )
}

fun Declarations.NameExpressionDeclarationProto.convert() : NameExpressionDeclaration {
    return when {
        name.hasIdentifier() -> IdentifierExpressionDeclaration(identifier = name.identifier.convert())
        name.hasQualifier() -> QualifierExpressionDeclaration(qualifier = name.qualifier.convert())
        else -> throw Exception("unknown nameExpression: ${this}")
    }
}

fun Declarations.NumericLiteralExpressionDeclarationProto.convert() = NumericLiteralExpressionDeclaration(value)
fun Declarations.BigIntLiteralExpressionDeclarationProto.convert() = BigIntLiteralExpressionDeclaration(value)
fun Declarations.StringLiteralExpressionDeclarationProto.convert() = StringLiteralExpressionDeclaration(value)
fun Declarations.BooleanLiteralExpressionDeclarationProto.convert() = BooleanLiteralExpressionDeclaration(value)
fun Declarations.RegExLiteralExpressionDeclarationProto.convert() = RegExLiteralExpressionDeclaration(value)
fun Declarations.ObjectLiteralExpressionDeclarationProto.convert() = ObjectLiteralExpressionDeclaration(membersList.map { it.convert() })

fun Declarations.LiteralExpressionDeclarationProto.convert() : LiteralExpressionDeclaration {
    return when {
        hasNumericLiteral() -> numericLiteral.convert()
        hasBigIntLiteral() -> bigIntLiteral.convert()
        hasStringLiteral() -> stringLiteral.convert()
        hasBooleanLiteral() -> booleanLiteral.convert()
        hasObjectLiteral() -> objectLiteral.convert()
        hasRegExLiteral() -> regExLiteral.convert()
        else -> throw Exception("unknown literalExpression: ${this}")
    }
}

fun Declarations.PropertyAccessExpressionDeclarationProto.convert() : PropertyAccessExpressionDeclaration {
    return PropertyAccessExpressionDeclaration(
            expression = expression.convert(),
            name = name.convert()
    )
}

fun Declarations.ElementAccessExpressionDeclarationProto.convert() : ElementAccessExpressionDeclaration {
    return ElementAccessExpressionDeclaration(
            expression = expression.convert(),
            argumentExpression = argumentExpression.convert()
    )
}

fun Declarations.UnknownExpressionDeclarationProto.convert() : UnknownExpressionDeclaration {
    return UnknownExpressionDeclaration(
            meta = meta
    )
}

fun Declarations.ExpressionDeclarationProto.convert() : ExpressionDeclaration {
    return when {
        hasBinaryExpression() -> binaryExpression.convert()
        hasUnaryExpression() -> unaryExpression.convert()
        hasTypeOfExpression() -> typeOfExpression.convert()
        hasCallExpression() -> callExpression.convert()
        hasNameExpression() -> nameExpression.convert()
        hasLiteralExpression() -> literalExpression.convert()
        hasPropertyAccessExpression() -> propertyAccessExpression.convert()
        hasElementAccessExpression() -> elementAccessExpression.convert()
        hasUnknownExpression() -> unknownExpression.convert()
        else -> throw Exception("unknown expression: ${this}")
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