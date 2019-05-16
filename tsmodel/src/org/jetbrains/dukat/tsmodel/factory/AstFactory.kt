package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
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
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PackageDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.astCommon.QualifierEntity
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

class AstFactory : AstNodeFactory<Entity> {

    override fun createDefinitionInfoDeclaration(fileName: String): Entity {
        return DefinitionInfoDeclaration(fileName)
    }

    override fun createTupleDeclaration(params: List<ParameterValueDeclaration>): Entity {
        return TupleDeclaration(params)
    }

    override fun createImportEqualsDeclaration(name: String, moduleReference: NameEntity, uid: String): ImportEqualsDeclaration {
        return ImportEqualsDeclaration(name, moduleReference, uid)
    }

    override fun createIdentifierDeclaration(value: String): Entity {
        return IdentifierEntity(value)
    }

    override fun createQualifiedNameDeclaration(left: NameEntity, right: IdentifierEntity): Entity {
        return QualifierEntity(left, right)
    }

    override fun createThisTypeDeclaration(): Entity {
        return ThisTypeDeclaration()
    }

    override fun createEnumDeclaration(name: String, values: List<EnumTokenDeclaration>): Entity {
        return EnumDeclaration(name, values)
    }

    override fun createEnumTokenDeclaration(value: String, meta: String): Entity {
        return EnumTokenDeclaration(value, meta)
    }

    override fun createExportAssignmentDeclaration(name: String, isExportEquals: Boolean) = ExportAssignmentDeclaration(name, isExportEquals)


    override fun createHeritageClauseDeclaration(name: NameEntity, typeArguments: List<ParameterValueDeclaration>, extending: Boolean) = HeritageClauseDeclaration(name, typeArguments, extending)

    override fun createTypeAliasDeclaration(
            aliasName: NameEntity,
            typeParameters: List<IdentifierEntity>,
            typeReference: ParameterValueDeclaration,
            uid: String
    ) = TypeAliasDeclaration(aliasName, typeParameters, typeReference, uid)

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
            members: List<MemberEntity>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): Entity = ClassDeclaration(name, members, typeParameters, parentEntities, modifiers, uid)

    override fun createObjectLiteral(members: List<MemberEntity>) = ObjectLiteralDeclaration(members)

    override fun createInterfaceDeclaration(name: String, members: List<MemberEntity>, typeParameters: List<TypeParameterDeclaration>, parentEntities: List<HeritageClauseDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String): Entity = InterfaceDeclaration(name, members, typeParameters, parentEntities, definitionsInfo, uid)

    override fun declareVariable(name: String, type: ParameterValueDeclaration, modifiers: List<ModifierDeclaration>, uid: String): Entity = VariableDeclaration(name, type, modifiers, uid)
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

    override fun createUnionTypeDeclaration(params: List<ParameterValueDeclaration>) = UnionTypeDeclaration(params)
    override fun createTypeDeclaration(value: NameEntity, params: Array<ParameterValueDeclaration>) = TypeDeclaration(value, params.toList())

    override fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?, vararg: Boolean, optional: Boolean) = ParameterDeclaration(name, type, initializer, vararg, optional)

    override fun createDocumentRoot(packageName: String, declarations: Array<TopLevelEntity>, modifiers: List<ModifierDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String, resourceName: String, root: Boolean) = PackageDeclaration(packageName, declarations.toList(), modifiers, definitionsInfo, uid, resourceName, root)

    override fun createSourceFileDeclaration(fileName: String, root: PackageDeclaration, referencedFiles: List<IdentifierEntity>): Entity = SourceFileDeclaration(fileName, root, referencedFiles)

    override fun createTypeParam(name: NameEntity, constraints: Array<ParameterValueDeclaration>) = TypeParameterDeclaration(name, constraints.toList())

    override fun createSourceSet(sources: List<SourceFileDeclaration>) = SourceSetDeclaration(sources)
}