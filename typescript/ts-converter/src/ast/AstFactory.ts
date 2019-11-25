import * as declarations from "declarations";
import {
  DefinitionInfoDeclaration,
  EnumTokenDeclaration,
  Expression,
  FunctionDeclaration,
  HeritageClauseDeclaration,
  IdentifierDeclaration,
  MemberDeclaration,
  ModifierDeclaration,
  ModuleDeclaration,
  NameEntity,
  ParameterDeclaration,
  TypeDeclaration,
  ReferenceEntity,
  SourceFileDeclaration,
  SourceSet,
  Declaration,
  TypeParamReferenceDeclaration,
  TypeParameter,
  TypeReferenceDeclaration
} from "./ast";
import {createLogger} from "../Logger";
import {TypeReferenceDeclarationProto} from "declarations";
import {HeritageClauseDeclarationProto} from "declarations";

export class AstFactory implements AstFactory {

  private log = createLogger("AstFactory");

  createCallSignatureDeclaration(parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>): MemberDeclaration {
    let callSignature = new declarations.CallSignatureDeclarationProto();
    callSignature.setParametersList(parameters);
    callSignature.setType(type);
    callSignature.setTypeparametersList(typeParams);

    let memberProto = new declarations.MemberDeclarationProto();
    memberProto.setCallsignature(callSignature);
    return memberProto;
  }

  createClassDeclaration(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclaration>, modifiers: Array<ModifierDeclaration>, uid: string): Declaration {
    let classDeclaration = new declarations.ClassDeclarationProto();
    classDeclaration.setName(name);
    classDeclaration.setModifiersList(modifiers);
    classDeclaration.setUid(uid);
    classDeclaration.setMembersList(members);
    classDeclaration.setTypeparametersList(typeParams);
    classDeclaration.setParententitiesList(parentEntities);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setClassdeclaration(classDeclaration);
    return topLevelDeclaration;
  }

  createConstructorDeclaration(parameters: Array<ParameterDeclaration>, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>): MemberDeclaration {
    let constructorDeclaration = new declarations.ConstructorDeclarationProto();

    constructorDeclaration.setParametersList(parameters);
    constructorDeclaration.setTypeparametersList(typeParams);
    constructorDeclaration.setModifiersList(modifiers);

    let memberProto = new declarations.MemberDeclarationProto();
    memberProto.setConstructordeclaration(constructorDeclaration);
    return memberProto;
  }

  createDefinitionInfoDeclaration(fileName: string): DefinitionInfoDeclaration {
    let definition = new declarations.DefinitionInfoDeclarationProto();
    definition.setFilename(fileName);
    return definition;
  }

  createEnumDeclaration(name: string, values: Array<EnumTokenDeclaration>, uid: string): Declaration {
    let enumDeclaration = new declarations.EnumDeclarationProto();
    enumDeclaration.setName(name);
    enumDeclaration.setValuesList(values);
    enumDeclaration.setUid(uid);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setEnumdeclaration(enumDeclaration);
    return topLevelDeclaration;
  }

  createEnumTokenDeclaration(value: string, meta: string): EnumTokenDeclaration {
    let enumToken = new declarations.EnumTokenDeclarationProto();
    enumToken.setValue(value);
    enumToken.setMeta(meta);
    return enumToken;
  }

  createExportAssignmentDeclaration(name: string, isExportEquals: boolean): Declaration {
    let exportAssignment = new declarations.ExportAssignmentDeclarationProto();
    exportAssignment.setName(name);
    exportAssignment.setIsexportequals(isExportEquals);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setExportassignment(exportAssignment);
    return topLevelDeclaration;
  }

  createExpression(kind: TypeReferenceDeclarationProto, meta: string): Expression {
    let expression = new declarations.ExpressionDeclarationProto();
    expression.setKind(kind);
    expression.setMeta(meta);

    return expression;
  }

  private createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, uid: string): FunctionDeclaration {
    let functionDeclaration = new declarations.FunctionDeclarationProto();
    functionDeclaration.setName(name);
    functionDeclaration.setParametersList(parameters);
    functionDeclaration.setType(type);
    functionDeclaration.setTypeparametersList(typeParams);
    functionDeclaration.setModifiersList(modifiers);
    functionDeclaration.setUid(uid);
    return functionDeclaration
  }

  createFunctionDeclarationAsMember(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, uid: string): MemberDeclaration {
    let functionDeclaration = this.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, uid);

    let memberProto = new declarations.MemberDeclarationProto();
    memberProto.setFunctiondeclaration(functionDeclaration);
    return memberProto;
  }

  createFunctionDeclarationAsTopLevel(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, uid: string): Declaration {
    let functionDeclaration = this.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, uid);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setFunctiondeclaration(functionDeclaration);
    return topLevelDeclaration;
  }

  createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: TypeDeclaration): TypeDeclaration {
    let functionType = new declarations.FunctionDeclarationProto();
    functionType.setParametersList(parameters);
    functionType.setType(type);

    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setFunctiontypedeclaration(functionType);
    return paramValueDeclaration;
  }

  createHeritageClauseDeclaration(name: NameEntity, typeArguments: Array<TypeDeclaration>, extending: boolean, typeReference: ReferenceEntity | null): HeritageClauseDeclaration {
    let heritageClauseDeclaration = new declarations.HeritageClauseDeclarationProto();

    heritageClauseDeclaration.setName(name);
    heritageClauseDeclaration.setTypeargumentsList(typeArguments);
    heritageClauseDeclaration.setExtending(extending);

    if (typeReference != null) {
      heritageClauseDeclaration.setTypereference(typeReference);
    }

    return heritageClauseDeclaration;
  }

  createIdentifierDeclarationAsNameEntity(value: string): NameEntity {
    let identifierProto = new declarations.IdentifierDeclarationProto();
    identifierProto.setValue(value);
    let nameEntity = new declarations.NameDeclarationProto();
    nameEntity.setIdentifier(identifierProto);
    return nameEntity;
  }

  createIdentifierDeclaration(value: string): IdentifierDeclaration {
    let identifierProto = new declarations.IdentifierDeclarationProto();
    identifierProto.setValue(value);
    return identifierProto;
  }

  createImportEqualsDeclaration(name: string, moduleReference: NameEntity, uid: string): Declaration {
    let importEqualsDeclaration = new declarations.ImportEqualsDeclarationProto();
    importEqualsDeclaration.setName(name);
    importEqualsDeclaration.setModulereference(moduleReference);
    importEqualsDeclaration.setUid(uid);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setImportequals(importEqualsDeclaration);
    return topLevelDeclaration;
  }

  createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: TypeDeclaration): MemberDeclaration {
    let indexSignatureDeclaration = new declarations.IndexSignatureDeclarationProto();
    indexSignatureDeclaration.setIndextypesList(indexTypes);
    indexSignatureDeclaration.setReturntype(returnType);

    let memberEntity = new declarations.MemberDeclarationProto();
    memberEntity.setIndexsignature(indexSignatureDeclaration);
    return memberEntity;
  }

  createInterfaceDeclaration(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclarationProto>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string): Declaration {
    let interfaceDeclaration = new declarations.InterfaceDeclarationProto();
    interfaceDeclaration.setName(name);
    interfaceDeclaration.setUid(uid);
    interfaceDeclaration.setDefinitionsinfoList(definitionsInfo);
    interfaceDeclaration.setMembersList(members);
    interfaceDeclaration.setTypeparametersList(typeParams);
    interfaceDeclaration.setParententitiesList(parentEntities);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setInterfacedeclaration(interfaceDeclaration);
    return topLevelDeclaration;
  }

  createIntersectionTypeDeclaration(params: Array<TypeDeclaration>): TypeDeclaration {
    let intersection = new declarations.IntersectionTypeDeclarationProto();
    intersection.setParamsList(params);

    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setIntersectiontype(intersection);
    return paramValueDeclaration;
  }

  createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>): MemberDeclaration {
    let methodDeclaration = new declarations.FunctionDeclarationProto();
    methodDeclaration.setName(name);
    methodDeclaration.setParametersList(parameters);
    methodDeclaration.setType(type);
    methodDeclaration.setTypeparametersList(typeParams);

    let memberProto = new declarations.MemberDeclarationProto();
    memberProto.setFunctiondeclaration(methodDeclaration);
    return memberProto;
  }

  createMethodSignatureDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): MemberDeclaration {
    let methodSignature = new declarations.MethodSignatureDeclarationProto();
    methodSignature.setName(name);
    methodSignature.setParametersList(parameters);
    methodSignature.setType(type);
    methodSignature.setTypeparametersList(typeParams);
    methodSignature.setOptional(optional);
    methodSignature.setModifiersList(modifiers);

    let memberProto = new declarations.MemberDeclarationProto();
    memberProto.setMethodsignature(methodSignature);
    return memberProto;
  }

  createModifierDeclaration(name: string): ModifierDeclaration {
    let modifierDeclaration = new declarations.ModifierDeclarationProto();
    modifierDeclaration.setToken(name);
    return modifierDeclaration;
  }

  createModuleDeclaration(packageName: NameEntity, moduleDeclarations: Declaration[], modifiers: Array<ModifierDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, resourceName: string, root: boolean): ModuleDeclaration {
    let moduleDeclaration = new declarations.ModuleDeclarationProto();
    moduleDeclaration.setPackagename(packageName);
    moduleDeclaration.setDeclarationsList(moduleDeclarations);
    moduleDeclaration.setModifiersList(modifiers);

    moduleDeclaration.setUid(uid);
    moduleDeclaration.setResourcename(resourceName);
    moduleDeclaration.setRoot(root);
    return moduleDeclaration;
  }

  createModuleDeclarationAsTopLevel(packageName: NameEntity, topLevels: Declaration[], modifiers: Array<ModifierDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, resourceName: string, root: boolean): Declaration {
    let module = this.createModuleDeclaration(packageName, topLevels, modifiers, definitionsInfo, uid, resourceName, root);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setModuledeclaration(module);
    return topLevelDeclaration;
  }

  createObjectLiteral(members: Array<MemberDeclaration>): TypeDeclaration {
    let objectLiteral = new declarations.ObjectLiteralDeclarationProto();
    objectLiteral.setMembersList(members);

    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setObjectliteral(objectLiteral);
    return paramValueDeclaration;
  }

  createParameterDeclaration(name: string, type: TypeDeclaration, initializer: Expression | null, vararg: boolean, optional: boolean): ParameterDeclaration {
    let parameterDeclaration = new declarations.ParameterDeclarationProto();
    parameterDeclaration.setName(name);
    parameterDeclaration.setType(type);
    if (initializer != null) {
      parameterDeclaration.setInitializer(initializer);
    }
    parameterDeclaration.setVararg(vararg);
    parameterDeclaration.setOptional(optional);
    return parameterDeclaration;
  }

  createQualifiedNameDeclaration(left: NameEntity, right: IdentifierDeclaration): NameEntity {
    let qualifier = new declarations.QualifierDeclarationProto();
    qualifier.setLeft(left);
    qualifier.setRight(right);
    
    let nameEntity = new declarations.NameDeclarationProto();
    nameEntity.setQualifier(qualifier);
    return nameEntity;
  }

  createReferenceEntity<T extends Declaration>(uid: string): ReferenceEntity {
    let reference = new declarations.ReferenceDeclarationProto();
    reference.setUid(uid);
    return reference;
  }

  createSourceFileDeclaration(fileName: string, root: ModuleDeclaration | null, referencedFiles: Array<string>): SourceFileDeclaration {
    let sourceFile = new declarations.SourceFileDeclarationProto();
    sourceFile.setFilename(fileName);
    sourceFile.setReferencedfilesList(referencedFiles);
    if (root) {
      sourceFile.setRoot(root);
    }
    return sourceFile;
  }

  createSourceSet(fileName: string, sources: Array<SourceFileDeclaration>): SourceSet {
    let sourceSet = new declarations.SourceSetDeclarationProto();
    sourceSet.setSourcename(fileName);
    sourceSet.setSourcesList(sources);
    return sourceSet;
  }

  createStringLiteralDeclaration(token: string): TypeDeclaration {
    let stringLiteral = new declarations.StringLiteralDeclarationProto();
    stringLiteral.setToken(token);

    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setStringliteral(stringLiteral);
    return paramValueDeclaration;
  }

  createThisTypeDeclaration(): TypeDeclaration {
    let parameterValueDeclaration = new declarations.ParameterValueDeclarationProto();
    parameterValueDeclaration.setThistype(new declarations.ThisTypeDeclarationProto());
    return parameterValueDeclaration;
  }

  createTupleDeclaration(params: Array<TypeDeclaration>): TypeDeclaration {
    let tupleDeclaration = new declarations.TupleDeclarationProto();
    tupleDeclaration.setParamsList(params);

    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setTupledeclaration(tupleDeclaration);
    return paramValueDeclaration;
  }

  createTypeAliasDeclaration(aliasName: NameEntity, typeParams: Array<IdentifierDeclaration>, typeReference: TypeDeclaration, uid: string): Declaration {
    let typeAlias = new declarations.TypeAliasDeclarationProto();
    typeAlias.setAliasname(aliasName);
    typeAlias.setTypeparametersList(typeParams);
    typeAlias.setTypereference(typeReference);
    typeAlias.setUid(uid);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setAliasdeclaration(typeAlias);
    return topLevelDeclaration;
  }

  createTypeReferenceDeclaration(value: NameEntity, params: Array<TypeDeclaration>, typeReference: ReferenceEntity | null = null): TypeReferenceDeclaration {
    let typeDeclaration = new declarations.TypeReferenceDeclarationProto();
    typeDeclaration.setValue(value);
    typeDeclaration.setParamsList(params);
    if (typeReference != null) {
      this.log.trace(`type reference for ${value} ${typeReference}`);
      typeDeclaration.setTypereference(typeReference);
    }

    return typeDeclaration;
  }

  createTypeParamReferenceDeclaration(value: NameEntity): TypeParamReferenceDeclaration {
    let typeDeclaration = new declarations.TypeParamReferenceDeclarationProto();
    typeDeclaration.setValue(value);
    return typeDeclaration;
  }

  createTypeParamReferenceDeclarationAsParamValue(value: NameEntity) {
    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setTypeparamreferencedeclaration(this.createTypeParamReferenceDeclaration(value));
    return paramValueDeclaration;
  }

  createTypeReferenceDeclarationAsParamValue(value: NameEntity, params: Array<TypeDeclaration>, typeReference: ReferenceEntity | null): TypeDeclaration {
    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setTypereferencedeclaration(this.createTypeReferenceDeclaration(value, params, typeReference));
    return paramValueDeclaration;
  }

  createTypeParam(name: NameEntity, constraints: Array<TypeDeclaration>, defaultValue: TypeDeclaration | null): TypeParameter {
    let typeParam = new declarations.TypeParameterDeclarationProto();
    typeParam.setName(name);
    typeParam.setConstraintsList(constraints);
    if (defaultValue) {
      typeParam.setDefaultvalue(defaultValue);
    }
    return typeParam;
  }

  createUnionTypeDeclaration(params: Array<TypeDeclaration>): TypeDeclaration {
    let unionTypeDeclaration = new declarations.UnionTypeDeclarationProto();
    unionTypeDeclaration.setParamsList(params);

    let paramValueDeclaration = new declarations.ParameterValueDeclarationProto();
    paramValueDeclaration.setUniontype(unionTypeDeclaration);
    return paramValueDeclaration;
  }

  declareProperty(name: string, type: TypeDeclaration, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): MemberDeclaration {
    let propertyDeclaration = new declarations.PropertyDeclarationProto();
    propertyDeclaration.setName(name);
    propertyDeclaration.setType(type);
    propertyDeclaration.setTypeparametersList(typeParams);
    propertyDeclaration.setOptional(optional);
    propertyDeclaration.setModifiersList(modifiers);

    let memberProto = new declarations.MemberDeclarationProto();
    memberProto.setProperty(propertyDeclaration);
    return memberProto;
  }

  declareVariable(name: string, type: TypeDeclaration, modifiers: Array<ModifierDeclaration>, uid: string): Declaration {
    let variableDeclaration = new declarations.VariableDeclarationProto();
    variableDeclaration.setName(name);
    variableDeclaration.setType(type);
    variableDeclaration.setModifiersList(modifiers);
    variableDeclaration.setUid(uid);

    let topLevelDeclaration = new declarations.TopLevelDeclarationProto();
    topLevelDeclaration.setVariabledeclaration(variableDeclaration);
    return topLevelDeclaration;
  }

}