package org.jetbrains.dukat.tsmodel.factory

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
import org.jetbrains.dukat.tsmodel.ThrowStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ArrayLiteralExpressionDeclaration
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
import org.jetbrains.dukat.tsmodelproto.ArrayLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BigIntLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BinaryExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BlockDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BooleanLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.CallExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.CallSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ClassDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ConditionalExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ConstructorDeclarationProto
import org.jetbrains.dukat.tsmodelproto.DefinitionInfoDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ElementAccessExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.EnumDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExportAssignmentDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExpressionStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.FunctionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.HeritageClauseDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IdentifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IfStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ImportEqualsDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IndexSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.InterfaceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.LiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.MemberDeclarationProto
import org.jetbrains.dukat.tsmodelproto.MethodSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ModifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ModuleDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NameDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NameExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NewExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NumericLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ObjectLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParameterDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParameterValueDeclarationProto
import org.jetbrains.dukat.tsmodelproto.PropertyAccessExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.PropertyDeclarationProto
import org.jetbrains.dukat.tsmodelproto.QualifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ReferenceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.RegExLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ReturnStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceFileDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceBundleDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceSetDeclarationProto
import org.jetbrains.dukat.tsmodelproto.StringLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ThrowStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TopLevelDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeAliasDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeOfExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeParameterDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeReferenceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.UnaryExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.UnknownExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.VariableDeclarationProto
import org.jetbrains.dukat.tsmodelproto.WhileStatementDeclarationProto

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

fun BlockDeclarationProto.convert() : BlockDeclaration {
    return BlockDeclaration(
            statements = statementsList.map { it.convert() }
    )
}

fun FunctionDeclarationProto.convert(): FunctionDeclaration {
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

fun TypeAliasDeclarationProto.convert(): TypeAliasDeclaration {
    return TypeAliasDeclaration(
            aliasName.convert(),
            typeParametersList.map { it.convert() },
            typeReference.convert(),
            uid
    )
}

fun VariableDeclarationProto.convert(): VariableDeclaration {
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

fun List<TopLevelDeclarationProto>.convert(): TopLevelDeclaration? {
    return when {
        this.isEmpty() -> null
        this.size == 1 -> this[0].convert()
        else -> BlockDeclaration( this.map { it.convert() } )
    }
}

fun IfStatementDeclarationProto.convert(): IfStatementDeclaration {
    return IfStatementDeclaration(
            condition = condition.convert(),
            thenStatement = thenStatementList.convert() ?: BlockDeclaration(emptyList()),
            elseStatement = elseStatementList.convert()
    )
}

fun WhileStatementDeclarationProto.convert(): WhileStatementDeclaration {
    return WhileStatementDeclaration(
            condition = condition.convert(),
            statement = statementList.convert() ?: BlockDeclaration(emptyList())
    )
}

fun ExpressionStatementDeclarationProto.convert(): ExpressionStatementDeclaration {
    return ExpressionStatementDeclaration(expression.convert())
}

fun ReturnStatementDeclarationProto.convert(): ReturnStatementDeclaration {
    return ReturnStatementDeclaration(
            if(hasExpression()) {
                expression.convert()
            } else null
    )
}

fun ThrowStatementDeclarationProto.convert(): ThrowStatementDeclaration {
    return ThrowStatementDeclaration(
            if(hasExpression()) {
                expression.convert()
            } else null
    )
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
        hasIfStatement() -> ifStatement.convert()
        hasWhileStatement() -> whileStatement.convert()
        hasExpressionStatement() -> expressionStatement.convert()
        hasReturnStatement() -> returnStatement.convert()
        hasThrowStatement() -> throwStatement.convert()
        hasBlockStatement() -> blockStatement.convert()
        else -> throw Exception("unknown TopLevelEntity: ${this}")
    }
}

fun PropertyDeclarationProto.convert(): PropertyDeclaration {
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
            modifiersList.map { it.convert() },
            if (hasBody()) {
                body.convert()
            } else null
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
                initializer.convert()
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


fun BinaryExpressionDeclarationProto.convert() : BinaryExpressionDeclaration {
    return BinaryExpressionDeclaration(
            left = left.convert(),
            operator = operator,
            right = right.convert()
    )
}

fun UnaryExpressionDeclarationProto.convert() : UnaryExpressionDeclaration {
    return UnaryExpressionDeclaration(
            operand = operand.convert(),
            operator = operator,
            isPrefix = isPrefix
    )
}

fun TypeOfExpressionDeclarationProto.convert(): TypeOfExpressionDeclaration {
    return TypeOfExpressionDeclaration(
            expression = expression.convert()
    )
}

fun CallExpressionDeclarationProto.convert(): CallExpressionDeclaration {
    return CallExpressionDeclaration(
            expression = expression.convert(),
            arguments = argumentsList.map { it.convert() }
    )
}

fun NameExpressionDeclarationProto.convert() : NameExpressionDeclaration {
    return when {
        name.hasIdentifier() -> IdentifierExpressionDeclaration(identifier = name.identifier.convert())
        name.hasQualifier() -> QualifierExpressionDeclaration(qualifier = name.qualifier.convert())
        else -> throw Exception("unknown nameExpression: ${this}")
    }
}

fun NumericLiteralExpressionDeclarationProto.convert() = NumericLiteralExpressionDeclaration(value)
fun BigIntLiteralExpressionDeclarationProto.convert() = BigIntLiteralExpressionDeclaration(value)
fun StringLiteralExpressionDeclarationProto.convert() = StringLiteralExpressionDeclaration(value)
fun BooleanLiteralExpressionDeclarationProto.convert() = BooleanLiteralExpressionDeclaration(value)
fun RegExLiteralExpressionDeclarationProto.convert() = RegExLiteralExpressionDeclaration(value)
fun ObjectLiteralExpressionDeclarationProto.convert() = ObjectLiteralExpressionDeclaration(membersList.map { it.convert() })
fun ArrayLiteralExpressionDeclarationProto.convert() = ArrayLiteralExpressionDeclaration(elementsList.map { it.convert() })

fun LiteralExpressionDeclarationProto.convert() : LiteralExpressionDeclaration {
    return when {
        hasNumericLiteral() -> numericLiteral.convert()
        hasBigIntLiteral() -> bigIntLiteral.convert()
        hasStringLiteral() -> stringLiteral.convert()
        hasBooleanLiteral() -> booleanLiteral.convert()
        hasObjectLiteral() -> objectLiteral.convert()
        hasArrayLiteral() -> arrayLiteral.convert()
        hasRegExLiteral() -> regExLiteral.convert()
        else -> throw Exception("unknown literalExpression: ${this}")
    }
}

fun PropertyAccessExpressionDeclarationProto.convert() : PropertyAccessExpressionDeclaration {
    return PropertyAccessExpressionDeclaration(
            expression = expression.convert(),
            name = name.convert()
    )
}

fun ElementAccessExpressionDeclarationProto.convert() : ElementAccessExpressionDeclaration {
    return ElementAccessExpressionDeclaration(
            expression = expression.convert(),
            argumentExpression = argumentExpression.convert()
    )
}

fun NewExpressionDeclarationProto.convert(): NewExpressionDeclaration {
    return NewExpressionDeclaration(
            expression = expression.convert(),
            arguments = argumentsList.map { it.convert() }
    )
}

fun ConditionalExpressionDeclarationProto.convert(): ConditionalExpressionDeclaration {
    return ConditionalExpressionDeclaration(
            condition = condition.convert(),
            whenTrue = whenTrue.convert(),
            whenFalse = whenFalse.convert()
    )
}

fun UnknownExpressionDeclarationProto.convert() : UnknownExpressionDeclaration {
    return UnknownExpressionDeclaration(
            meta = meta
    )
}

fun ExpressionDeclarationProto.convert() : ExpressionDeclaration {
    return when {
        hasBinaryExpression() -> binaryExpression.convert()
        hasUnaryExpression() -> unaryExpression.convert()
        hasFunctionExpression() -> functionExpression.convert()
        hasClassExpression() -> classExpression.convert()
        hasTypeOfExpression() -> typeOfExpression.convert()
        hasCallExpression() -> callExpression.convert()
        hasNameExpression() -> nameExpression.convert()
        hasLiteralExpression() -> literalExpression.convert()
        hasPropertyAccessExpression() -> propertyAccessExpression.convert()
        hasElementAccessExpression() -> elementAccessExpression.convert()
        hasNewExpression() -> newExpression.convert()
        hasConditionalExpression() -> conditionalExpression.convert()
        hasUnknownExpression() -> unknownExpression.convert()
        else -> throw Exception("unknown expression: ${this}")
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