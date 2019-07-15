package org.jetbrains.dukat.tsmodel.factory

import org.jetbrains.dukat.astCommon.Entity
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
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
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
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
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

class AstFactory {

    fun createDefinitionInfoDeclaration(fileName: String): Entity {
        return DefinitionInfoDeclaration(fileName)
    }

    fun createTupleDeclaration(params: List<ParameterValueDeclaration>): Entity {
        return TupleDeclaration(params)
    }

    fun createImportEqualsDeclaration(name: String, moduleReference: NameEntity, uid: String): ImportEqualsDeclaration {
        return ImportEqualsDeclaration(name, moduleReference, uid)
    }

    fun createIdentifierDeclaration(value: String): Entity {
        return IdentifierEntity(value)
    }

    fun createQualifiedNameDeclaration(left: NameEntity, right: IdentifierEntity): Entity {
        return QualifierEntity(left, right)
    }

    fun createThisTypeDeclaration(): Entity {
        return ThisTypeDeclaration()
    }

    fun createEnumDeclaration(name: String, values: List<EnumTokenDeclaration>): Entity {
        return EnumDeclaration(name, values)
    }

    fun createEnumTokenDeclaration(value: String, meta: String): Entity {
        return EnumTokenDeclaration(value, meta)
    }

    fun createExportAssignmentDeclaration(name: String, isExportEquals: Boolean) = ExportAssignmentDeclaration(name, isExportEquals)


    fun createHeritageClauseDeclaration(name: NameEntity, typeArguments: List<ParameterValueDeclaration>, extending: Boolean) = HeritageClauseDeclaration(name, typeArguments, extending)

    fun createTypeAliasDeclaration(
            aliasName: NameEntity,
            typeParameters: List<IdentifierEntity>,
            typeReference: ParameterValueDeclaration,
            uid: String
    ) = TypeAliasDeclaration(aliasName, typeParameters, typeReference, uid)

    fun createStringLiteralDeclaration(token: String) = StringLiteralDeclaration(token)

    fun createIndexSignatureDeclaration(indexTypes: List<ParameterDeclaration>, returnType: ParameterValueDeclaration) = IndexSignatureDeclaration(indexTypes, returnType)

    fun createCallSignatureDeclaration(
            parameters: List<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: List<TypeParameterDeclaration>
    ) = CallSignatureDeclaration(parameters, type, typeParameters)

    fun createModifierDeclaration(token: String) = ModifierDeclaration(token)

    fun createClassDeclaration(
            name: NameEntity,
            members: List<MemberEntity>,
            typeParameters: List<TypeParameterDeclaration>,
            parentEntities: List<HeritageClauseDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): Entity = ClassDeclaration(name, members, typeParameters, parentEntities, modifiers, uid)

    fun createObjectLiteral(members: List<MemberEntity>) = ObjectLiteralDeclaration(members)

    fun createInterfaceDeclaration(name: NameEntity, members: List<MemberEntity>, typeParameters: List<TypeParameterDeclaration>, parentEntities: List<HeritageClauseDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String): Entity = InterfaceDeclaration(name, members, typeParameters, parentEntities, definitionsInfo, uid)

    fun declareVariable(name: String, type: ParameterValueDeclaration, modifiers: List<ModifierDeclaration>, uid: String): Entity = VariableDeclaration(name, type, modifiers, uid)
    fun declareProperty(
            name: String,
            type: ParameterValueDeclaration,
            parameters: List<TypeParameterDeclaration>,
            optional: Boolean,
            modifiers: List<ModifierDeclaration>
    ) = PropertyDeclaration(name, type, parameters, optional, modifiers)

    fun createExpression(kind: TypeDeclaration, meta: String?) = ExpressionDeclaration(kind, meta)

    fun createConstructorDeclaration(parameters: List<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: List<TypeParameterDeclaration>, modifiers: List<ModifierDeclaration>) = ConstructorDeclaration(parameters, type, typeParameters, modifiers)

    fun createFunctionDeclaration(
            name: String,
            parameters: Array<ParameterDeclaration>,
            type: ParameterValueDeclaration,
            typeParameters: Array<TypeParameterDeclaration>,
            modifiers: List<ModifierDeclaration>,
            uid: String
    ): FunctionDeclaration {
        return FunctionDeclaration(name, parameters.toList(), type, typeParameters.toList(), modifiers, uid)
    }

    fun createMethodSignatureDeclaration(name: String, parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration, typeParameters: Array<TypeParameterDeclaration>, optional: Boolean, modifiers: List<ModifierDeclaration>) = MethodSignatureDeclaration(name, parameters.toList(), type, typeParameters.toList(), optional, modifiers)

    fun createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: ParameterValueDeclaration) = FunctionTypeDeclaration(parameters.toList(), type)

    fun createIntersectionTypeDeclaration(params: List<ParameterValueDeclaration>) = IntersectionTypeDeclaration(params)

    fun createUnionTypeDeclaration(params: List<ParameterValueDeclaration>) = UnionTypeDeclaration(params)
    fun createTypeDeclaration(value: NameEntity, params: Array<ParameterValueDeclaration>, typeReference: String?) = TypeDeclaration(value, params.toList(), typeReference)

    fun createParameterDeclaration(name: String, type: ParameterValueDeclaration, initializer: ExpressionDeclaration?, vararg: Boolean, optional: Boolean) = ParameterDeclaration(name, type, initializer, vararg, optional)

    fun createModuleDeclaration(packageName: NameEntity, declarations: Array<TopLevelEntity>, modifiers: List<ModifierDeclaration>, definitionsInfo: List<DefinitionInfoDeclaration>, uid: String, resourceName: String, root: Boolean) = ModuleDeclaration(packageName, declarations.toList(), modifiers, definitionsInfo, uid, resourceName, root)

    fun createSourceFileDeclaration(fileName: String, root: ModuleDeclaration, referencedFiles: List<IdentifierEntity>): Entity = SourceFileDeclaration(fileName, root, referencedFiles)

    fun createTypeParam(name: NameEntity, constraints: Array<ParameterValueDeclaration>) = TypeParameterDeclaration(name, constraints.toList())

    fun createSourceSet(sources: List<SourceFileDeclaration>) = SourceSetDeclaration(sources)
}