package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.tsmodel.ArrayDestructuringDeclaration
import org.jetbrains.dukat.tsmodel.BindingElementDeclaration
import org.jetbrains.dukat.tsmodel.BindingVariableDeclaration
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.BreakStatementDeclaration
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.CaseDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ContinueStatementDeclaration
import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.EnumTokenDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.expression.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForOfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ForStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.expression.NonNullExpressionDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceKindDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceOriginDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.SwitchStatementDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.ThrowStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.VariableLikeDeclaration
import org.jetbrains.dukat.tsmodel.WhileStatementDeclaration
import org.jetbrains.dukat.tsmodel.expression.AsExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.BinaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.CallExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ConditionalExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ElementAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.NewExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.ParenthesizedExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.PropertyAccessExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.SpreadExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.TypeOfExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnaryExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.UnknownExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.YieldExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ArrayLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BigIntLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.BooleanLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.LiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.NumericLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.ObjectLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.RegExLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.literal.StringLiteralExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.IdentifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.NameExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.name.QualifierExpressionDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.ExpressionTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.StringTemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.expression.templates.TemplateTokenDeclaration
import org.jetbrains.dukat.tsmodel.importClause.ImportDeclaration
import org.jetbrains.dukat.tsmodel.importClause.ImportSpecifierDeclaration
import org.jetbrains.dukat.tsmodel.importClause.NamedImportsDeclaration
import org.jetbrains.dukat.tsmodel.importClause.NamespaceImportDeclaration
import org.jetbrains.dukat.tsmodel.importClause.ReferenceClauseDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.NumericLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodelproto.ArrayDestructuringDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ArrayLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.AsExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BigIntLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BinaryExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BindingElementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BindingVariableDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BlockDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BooleanLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.BreakStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.CallExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.CallSignatureDeclarationProto
import org.jetbrains.dukat.tsmodelproto.CaseDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ClassDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ConditionalExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ConstructorDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ContinueStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.DefinitionInfoDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ElementAccessExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.EnumDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExportAssignmentDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ExpressionStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ForOfStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ForStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.FunctionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.HeritageClauseDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IdentifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.IfStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ImportClauseDeclarationProto
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
import org.jetbrains.dukat.tsmodelproto.NonNullExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.NumericLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ObjectLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParameterDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParameterValueDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ParenthesizedExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.PropertyAccessExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.PropertyDeclarationProto
import org.jetbrains.dukat.tsmodelproto.QualifierDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ReferenceClauseDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ReferenceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.RegExLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ReturnStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceFileDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SourceSetDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SpreadExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.StatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.StringLiteralExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.SwitchStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TemplateExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TemplateTokenDeclarationProto
import org.jetbrains.dukat.tsmodelproto.ThrowStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TopLevelDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeAliasDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeOfExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeParameterDeclarationProto
import org.jetbrains.dukat.tsmodelproto.TypeReferenceDeclarationProto
import org.jetbrains.dukat.tsmodelproto.UnaryExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.UnknownExpressionDeclarationProto
import org.jetbrains.dukat.tsmodelproto.VariableDeclarationProto
import org.jetbrains.dukat.tsmodelproto.VariableLikeDeclarationProto
import org.jetbrains.dukat.tsmodelproto.VariableLikeDeclarationProtoOrBuilder
import org.jetbrains.dukat.tsmodelproto.WhileStatementDeclarationProto
import org.jetbrains.dukat.tsmodelproto.YieldExpressionDeclarationProto

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

fun ModifierDeclarationProto.convert(): ModifierDeclaration? {
    return when(token) {
        ModifierDeclarationProto.MODIFIER_KIND.DECLARE -> ModifierDeclaration.DECLARE_KEYWORD
        ModifierDeclarationProto.MODIFIER_KIND.DEFAULT -> ModifierDeclaration.DEFAULT_KEYWORD
        ModifierDeclarationProto.MODIFIER_KIND.EXPORT -> ModifierDeclaration.EXPORT_KEYWORD
        ModifierDeclarationProto.MODIFIER_KIND.STATIC -> ModifierDeclaration.STATIC_KEYWORD
        else -> null
    }
}

fun ReferenceDeclarationProto.convert(): ReferenceDeclaration {
    val origin = when (origin) {
        ReferenceDeclarationProto.ORIGIN.IMPORT -> ReferenceOriginDeclaration.IMPORT
        ReferenceDeclarationProto.ORIGIN.NAMED_IMPORT -> ReferenceOriginDeclaration.NAMED_IMPORT
        else -> ReferenceOriginDeclaration.IRRELEVANT
    }
    val kind = when (kind) {
        ReferenceDeclarationProto.KIND.CLASS -> ReferenceKindDeclaration.CLASS
        ReferenceDeclarationProto.KIND.INTERFACE -> ReferenceKindDeclaration.INTERFACE
        ReferenceDeclarationProto.KIND.TYPEALIAS -> ReferenceKindDeclaration.TYPEALIAS
        else -> ReferenceKindDeclaration.IRRELEVANT
    }
    return ReferenceDeclaration(uid, origin, kind)
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
            modifiersList.mapNotNull { it.convert() }.toSet(),
            definitionsInfoList.map { it.convert() },
            uid
    )
}

fun InterfaceDeclarationProto.convert(): InterfaceDeclaration {
    return InterfaceDeclaration(
            name.convert(),
            membersList.map { it.convert() },
            typeParametersList.map { it.convert() },
            parentEntitiesList.map { it.convert() },
            modifiersList.mapNotNull { it.convert() }.toSet(),
            definitionsInfoList.map { it.convert() },
            uid
    )
}

fun BlockDeclarationProto.convert(): BlockDeclaration {
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
            modifiersList.mapNotNull { it.convert() }.toSet(),
            if (hasBody()) {
                body.convert()
            } else null,
            definitionsInfoList.map { it.convert() },
            uid,
            isGenerator
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
            modifiersList.mapNotNull { it.convert() }.toSet(),
            if (hasInitializer()) {
                initializer?.convert()
            } else null,
            definitionsInfoList.map { it.convert() },
            uid,
            hasType
    )
}

fun EnumDeclarationProto.convert(): EnumDeclaration {
    return EnumDeclaration(name, valuesList.map { EnumTokenDeclaration(it.value, it.meta) }, uid)
}

private fun DefinitionInfoDeclarationProto.convert(): DefinitionInfoDeclaration {
    return DefinitionInfoDeclaration(uid, fileName)
}

private fun ImportClauseDeclarationProto.convert(): ImportDeclaration? {
    return when {
        hasNamespaceImport() -> NamespaceImportDeclaration(namespaceImport.name)
        hasNamedImports() -> NamedImportsDeclaration(namedImports.importSpecifiersList.map { ImportSpecifierDeclaration(it.name, it.propertyName, it.uid) })
        else -> null
    }?.let { clause ->
        ImportDeclaration(clause, referencedFile)
    }
}

private fun ReferenceClauseDeclarationProto.convert(): ReferenceClauseDeclaration {
    return ReferenceClauseDeclaration(path, referencedFile)
}

fun ModuleDeclarationProto.convert(): ModuleDeclaration {

    var export: ExportAssignmentDeclaration? = null
    val declarations = declarationsList.mapNotNull {
        if (it.hasExportAssignment()) {
            export = it.exportAssignment.convert()
            null
        } else {
            it.convert()
        }
    }

    return ModuleDeclaration(
            name = if (hasPackageName()) { packageName.convert() } else null,
            imports = importsList.mapNotNull { it.convert() },
            references = referencesList.map { it.convert() },
            export = export,
            declarations = declarations,
            modifiers = modifiersList.mapNotNull { it.convert() }.toSet(),
            definitionsInfo = definitionsInfoList.map { it.convert() },
            uid = uid,
            resourceName = resourceName,
            kind = when (kind) {
                ModuleDeclarationProto.MODULE_KIND.DECLARATION_FILE -> ModuleDeclarationKind.DECLARATION_FILE
                ModuleDeclarationProto.MODULE_KIND.SOURCE_FILE -> ModuleDeclarationKind.SOURCE_FILE
                ModuleDeclarationProto.MODULE_KIND.AMBIENT_MODULE -> ModuleDeclarationKind.AMBIENT_MODULE
                ModuleDeclarationProto.MODULE_KIND.NAMESPACE -> ModuleDeclarationKind.NAMESPACE
                else -> ModuleDeclarationKind.SOURCE_FILE
            },
            isLib = isLibSource
    )
}

fun ExportAssignmentDeclarationProto.convert(): ExportAssignmentDeclaration {
    return ExportAssignmentDeclaration(uidList, isExportEquals)
}

fun ImportEqualsDeclarationProto.convert(): ImportEqualsDeclaration {
    return ImportEqualsDeclaration(name, moduleReference.convert(), uid)
}

fun List<StatementDeclarationProto>.convert(): BlockDeclaration? {
    return when {
        this.isEmpty() -> null
        this.size == 1 -> {
            val statement = this[0].convert()
            if (statement is BlockDeclaration) {
                statement
            } else {
                BlockDeclaration(listOf(statement))
            }
        }
        else -> BlockDeclaration(this.map { it.convert() })
    }
}

fun IfStatementDeclarationProto.convert(): IfStatementDeclaration {
    return IfStatementDeclaration(
            condition = condition.convert(),
            thenStatement = thenStatementList.convert() ?: BlockDeclaration(emptyList()),
            elseStatement = elseStatementList.convert()
    )
}

fun ForStatementDeclarationProto.convert(): ForStatementDeclaration {
    return ForStatementDeclaration(
            initializer = BlockDeclaration(initializerList.map { it.convert() }),
            condition = if (hasCondition()) condition.convert() else null,
            incrementor = if (hasIncrementor()) incrementor.convert() else null,
            body = statementList.convert() ?: BlockDeclaration(emptyList())
    )
}

fun ForOfStatementDeclarationProto.convert(): ForOfStatementDeclaration {
    return ForOfStatementDeclaration(
        variable = variable.convert() as VariableLikeDeclaration,
        expression = expression.convert(),
        body = statementList.convert() ?: BlockDeclaration(emptyList())
    )
}

fun WhileStatementDeclarationProto.convert(): WhileStatementDeclaration {
    return WhileStatementDeclaration(
            condition = condition.convert(),
            statement = statementList.convert() ?: BlockDeclaration(emptyList())
    )
}

fun CaseDeclarationProto.convert(): CaseDeclaration {
    return CaseDeclaration(
        condition = if (hasCondition()) {
            condition.convert()
        } else {
            null
        },
        body = statementList.convert() ?: BlockDeclaration(emptyList())
    )
}

fun SwitchStatementDeclarationProto.convert(): SwitchStatementDeclaration {
    return SwitchStatementDeclaration(
        expression = expression.convert(),
        cases = caseList.map { it.convert() }
    )
}

fun ExpressionStatementDeclarationProto.convert(): ExpressionStatementDeclaration {
    return ExpressionStatementDeclaration(expression.convert())
}

fun ReturnStatementDeclarationProto.convert(): ReturnStatementDeclaration {
    return ReturnStatementDeclaration(
            if (hasExpression()) {
                expression.convert()
            } else null
    )
}

fun BreakStatementDeclarationProto.convert(): BreakStatementDeclaration {
    return BreakStatementDeclaration()
}

fun ContinueStatementDeclarationProto.convert(): ContinueStatementDeclaration {
    return ContinueStatementDeclaration()
}

fun ThrowStatementDeclarationProto.convert(): ThrowStatementDeclaration {
    return ThrowStatementDeclaration(
        expression.convert()
    )
}

fun TopLevelDeclarationProto.convert(): TopLevelDeclaration {
    return when {
        hasClassDeclaration() -> classDeclaration.convert()
        hasInterfaceDeclaration() -> interfaceDeclaration.convert()
        hasAliasDeclaration() -> aliasDeclaration.convert()
        hasEnumDeclaration() -> enumDeclaration.convert()
        hasModuleDeclaration() -> moduleDeclaration.convert()
        hasImportEquals() -> importEquals.convert()
        hasStatement() -> statement.convert()
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
            modifiersList.mapNotNull { it.convert() }.toSet(),
            hasType
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
            modifiersList.mapNotNull { it.convert() }.toSet(),
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
            modifiersList.mapNotNull { it.convert() }.toSet()
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
            optional,
            hasType
    )
}

private fun ParameterValueDeclarationProto.convert(): ParameterValueDeclaration {
    return when {
        hasStringLiteral() -> StringLiteralDeclaration(stringLiteral.token)
        hasNumericLiteral() -> NumericLiteralDeclaration(numericLiteral.token)
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


fun BinaryExpressionDeclarationProto.convert(): BinaryExpressionDeclaration {
    return BinaryExpressionDeclaration(
            left = left.convert(),
            operator = operator,
            right = right.convert()
    )
}

fun UnaryExpressionDeclarationProto.convert(): UnaryExpressionDeclaration {
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
            arguments = argumentsList.map { it.convert() },
            typeArguments = typeArgumentsList.map { it.convert() }
    )
}

fun NameExpressionDeclarationProto.convert(): NameExpressionDeclaration {
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

fun LiteralExpressionDeclarationProto.convert(): LiteralExpressionDeclaration {
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

fun TemplateTokenDeclarationProto.convert(): TemplateTokenDeclaration {
    return when  {
        hasExpression() -> ExpressionTemplateTokenDeclaration(expression.convert())
        hasStringLiteral() -> StringTemplateTokenDeclaration(stringLiteral.convert())
        else -> throw Exception("unknown templateToken: ${this}")
    }
}

fun TemplateExpressionDeclarationProto.convert(): TemplateExpressionDeclaration {
    return TemplateExpressionDeclaration(
        tokenList.map { it.convert() }
    )
}

fun PropertyAccessExpressionDeclarationProto.convert(): PropertyAccessExpressionDeclaration {
    return PropertyAccessExpressionDeclaration(
            expression = expression.convert(),
            name = name.convert()
    )
}

fun ElementAccessExpressionDeclarationProto.convert(): ElementAccessExpressionDeclaration {
    return ElementAccessExpressionDeclaration(
            expression = expression.convert(),
            argumentExpression = argumentExpression.convert()
    )
}

fun NewExpressionDeclarationProto.convert(): NewExpressionDeclaration {
    return NewExpressionDeclaration(
            expression = expression.convert(),
            arguments = argumentsList.map { it.convert() },
            typeArguments = typeArgumentsList.map { it.convert() }
    )
}

fun ConditionalExpressionDeclarationProto.convert(): ConditionalExpressionDeclaration {
    return ConditionalExpressionDeclaration(
            condition = condition.convert(),
            whenTrue = whenTrue.convert(),
            whenFalse = whenFalse.convert()
    )
}

fun UnknownExpressionDeclarationProto.convert(): UnknownExpressionDeclaration {
    return UnknownExpressionDeclaration(
            meta = meta
    )
}

fun AsExpressionDeclarationProto.convert(): AsExpressionDeclaration {
    return AsExpressionDeclaration(
        expression = expression.convert(),
        type = type.convert()
    )
}

fun NonNullExpressionDeclarationProto.convert(): NonNullExpressionDeclaration {
    return NonNullExpressionDeclaration(
        expression = expression.convert()
    )
}

fun ParenthesizedExpressionDeclarationProto.convert(): ParenthesizedExpressionDeclaration {
    return ParenthesizedExpressionDeclaration(
        expression = expression.convert()
    )
}

fun SpreadExpressionDeclarationProto.convert(): SpreadExpressionDeclaration {
    return SpreadExpressionDeclaration(
        expression = expression.convert()
    )
}

fun YieldExpressionDeclarationProto.convert(): YieldExpressionDeclaration {
    return YieldExpressionDeclaration(
        expression = if (hasExpression()) expression.convert() else null,
        hasAsterisk = hasAsterisk
    )
}

fun BindingVariableDeclarationProto.convert(): BindingVariableDeclaration {
    return BindingVariableDeclaration(
        name = name,
        initializer = if (hasExpression()) expression.convert() else null
    )
}

fun BindingElementDeclarationProto.convert(): BindingElementDeclaration {
    return when {
        hasArrayDestructuring() -> arrayDestructuring.convert()
        hasBindingVariable() -> bindingVariable.convert()
        else -> throw Exception("unknown binding element: $this")
    }
}

fun ArrayDestructuringDeclarationProto.convert(): ArrayDestructuringDeclaration {
    return ArrayDestructuringDeclaration(
        elements = elementsList.map { it.convert() }
    )
}

fun VariableLikeDeclarationProto.convert(): VariableLikeDeclaration {
    return when {
        hasVariable() -> variable.convert()
        hasArrayDestructuring() -> arrayDestructuring.convert()
        else -> throw Exception("unknown variable-like declaration: $this")
    }
}

fun ExpressionDeclarationProto.convert(): ExpressionDeclaration {
    return when {
        hasBinaryExpression() -> binaryExpression.convert()
        hasUnaryExpression() -> unaryExpression.convert()
        hasFunctionExpression() -> functionExpression.convert()
        hasClassExpression() -> classExpression.convert()
        hasTypeOfExpression() -> typeOfExpression.convert()
        hasCallExpression() -> callExpression.convert()
        hasNameExpression() -> nameExpression.convert()
        hasLiteralExpression() -> literalExpression.convert()
        hasTemplateExpression() -> templateExpression.convert()
        hasPropertyAccessExpression() -> propertyAccessExpression.convert()
        hasElementAccessExpression() -> elementAccessExpression.convert()
        hasNewExpression() -> newExpression.convert()
        hasConditionalExpression() -> conditionalExpression.convert()
        hasAsExpression() -> asExpression.convert()
        hasNonNullExpression() -> nonNullExpression.convert()
        hasYieldExpression() -> yieldExpression.convert()
        hasParenthesizedExpression() -> parenthesizedExpression.convert()
        hasSpreadExpression() -> spreadExpression.convert()
        hasUnknownExpression() -> unknownExpression.convert()
        else -> throw Exception("unknown expression: ${this}")
    }
}

fun StatementDeclarationProto.convert(): StatementDeclaration {
    return when {
        hasIfStatement() -> ifStatement.convert()
        hasWhileStatement() -> whileStatement.convert()
        hasExpressionStatement() -> expressionStatement.convert()
        hasReturnStatement() -> returnStatement.convert()
        hasBreakStatement() -> breakStatement.convert()
        hasContinueStatement() -> continueStatement.convert()
        hasThrowStatement() -> throwStatement.convert()
        hasBlockStatement() -> blockStatement.convert()
        hasVariableLikeDeclaration() -> variableLikeDeclaration.convert()
        hasFunctionDeclaration() -> functionDeclaration.convert()
        hasForStatement() -> forStatement.convert()
        hasSwitchStatement() -> switchStatement.convert()
        hasForOfStatement() -> forOfStatement.convert()
        else -> throw Exception("unknown statement: ${this}")
    }
}

fun SourceFileDeclarationProto.convert(): SourceFileDeclaration {
    return SourceFileDeclaration(
            fileName,
            root.convert()
    )
}

fun SourceSetDeclarationProto.convert(): SourceSetDeclaration {
    return SourceSetDeclaration(sourceNameList, sourcesList.map { it.convert() })
}