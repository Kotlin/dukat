package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.AstNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
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
import org.jetbrains.dukat.tsmodel.HeritageSymbolDeclaration
import org.jetbrains.dukat.tsmodel.IdentifierDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleReferenceDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyAccessDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedLeftDeclaration
import org.jetbrains.dukat.tsmodel.QualifiedNamedDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

class AstFactory : AstNodeFactory<AstNode> {

    override fun createDefinitionInfoDeclaration(fileName: String): AstNode {
        return DefinitionInfoDeclaration(fileName)
    }

    override fun createTupleDeclaration(params: List<ParameterValueDeclaration>): AstNode {
        return TupleDeclaration(params)
    }

    override fun createPropertyAccessDeclaration(name: IdentifierDeclaration, expression: HeritageSymbolDeclaration): PropertyAccessDeclaration {
        return PropertyAccessDeclaration(name, expression)
    }

    override fun createImportEqualsDeclaration(name: String, moduleReference: ModuleReferenceDeclaration): ImportEqualsDeclaration {
        return ImportEqualsDeclaration(name, moduleReference)
    }

    override fun createIdentifierDeclaration(value: String): AstNode {
        return IdentifierDeclaration(value)
    }

    override fun createQualifiedNameDeclaration(left: QualifiedLeftDeclaration, right: IdentifierDeclaration): AstNode {
        return QualifiedNamedDeclaration(left, right)
    }

    override fun createThisTypeDeclaration(): AstNode {
        return ThisTypeDeclaration()
    }

    override fun createEnumDeclaration(name: String, values: List<EnumTokenDeclaration>): AstNode {
        return EnumDeclaration(name, values)
    }

    override fun createEnumTokenDeclaration(value: String, meta: String): AstNode {
        return EnumTokenDeclaration(value, meta)
    }

    override fun createExportAssignmentDeclaration(name: String, isExportEquals: Boolean) = ExportAssignmentDeclaration(name, isExportEquals)


    override fun createHeritageClauseDeclaration(name: HeritageSymbolDeclaration, typeArguments: List<IdentifierDeclaration>, extending: Boolean) = HeritageClauseDeclaration(name, typeArguments, extending)

    override fun createTypeAliasDeclaration(aliasName: String, typeParameters: List<IdentifierDeclaration>, typeReference: ParameterValueDeclaration) = TypeAliasDeclaration(aliasName, typeParameters, typeReference)

    override fun createStringTypeDeclaration(tokens: List<String>) = StringTypeDeclaration(tokens)

    override fun createIndexSignatureDeclaration(indexTypes: List<ParameterDeclaration>, returnType: ParameterValueDeclaration) = IndexSignatureDeclaration(indexTypes, returnType)

    override fun createCallSignatureDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>
    ) = CallSignatureDeclaration(parameters, type, typeParameters)

    override fun createModifierDeclaration(token: String) = ModifierDeclaration(token)

    override fun createClassDeclaration(
            name: String,
            members: List<MemberDeclaration>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): AstNode = ClassDeclaration(name, members, typeParameters, parentEntities, modifiers, uid)

    override fun createObjectLiteral(members: List<MemberDeclaration>) = ObjectLiteralDeclaration(members)

    override fun createInterfaceDeclaration(name: String, members: List<MemberDeclaration>, typeParameters: List<TypeParameterDeclaration>, parentEntities: List<HeritageClauseDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String): AstNode = InterfaceDeclaration(name, members, typeParameters, parentEntities, definitionsInfo, uid)

    override fun declareVariable(name: String, type: ParameterValueDeclaration, modifiers: List<ModifierDeclaration>, uid: String): AstNode = VariableDeclaration(name, type, modifiers, uid)
    override fun declareProperty(
            name: String,
            type: ParameterValueDeclaration,
            parameters: List<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>
    ) = PropertyDeclaration(name, type, parameters, optional, modifiers)

    override fun createExpression(kind: TypeDeclaration, meta: String?) = ExpressionDeclaration(kind, meta)

    override fun createConstructorDeclaration(parameters: List<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: List<TypeParameterDeclaration>, modifiers: List<ModifierDeclaration>) = ConstructorDeclaration(parameters, type, typeParameters, modifiers)

    override fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): FunctionDeclaration {
        return FunctionDeclaration(name, parameters.toList(), type, typeParameters.toList(), modifiers, uid)
    }

    override fun createMethodSignatureDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: Array<TypeParameterDeclaration>, optional: Boolean, modifiers: List<ModifierDeclaration>) = MethodSignatureDeclaration(name, parameters.toList(), type, typeParameters.toList(), optional, modifiers)

    override fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration) = FunctionTypeDeclaration(parameters.toList(), type)

    override fun createIntersectionTypeDeclaration(params: List<ParameterValueDeclaration>) = IntersectionTypeDeclaration(params)

    override fun createUnionDeclaration(params: List<ParameterValueDeclaration>) = UnionTypeDeclaration(params)
    override fun createTypeDeclaration(value: String, params: Array<ParameterValueDeclaration>) = TypeDeclaration(value, params)

    override fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?, vararg: Boolean, optional: Boolean) = ParameterDeclaration(name, type, initializer, vararg, optional)

    override fun createDocumentRoot(packageName: String, declarations: Array<TopLevelDeclaration>, modifiers: List<ModifierDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String, resourceName: String)
            = PackageDeclaration(packageName, declarations.toList(), modifiers, definitionsInfo, uid, resourceName)

    override fun createSourceFileDeclaration(fileName: String, root: PackageDeclaration, referencedFiles: List<IdentifierDeclaration>): AstNode
            = SourceFileDeclaration(fileName, root, referencedFiles)

    override fun createTypeParam(name: String, constraints: Array<ParameterValueDeclaration>) = TypeParameterDeclaration(name, constraints.toList())

    override fun createSourceSet(sources: List<SourceFileDeclaration>)
        = SourceSetDeclaration(sources)
}