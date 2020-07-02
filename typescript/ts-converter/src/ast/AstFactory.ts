import {
  Block,
  BindingElementDeclaration,
  ClassDeclaration,
  Declaration,
  DefinitionInfoDeclaration,
  EnumTokenDeclaration,
  Expression,
  FunctionDeclaration,
  HeritageClauseDeclaration,
  IdentifierDeclaration,
  ImportClauseDeclaration,
  ImportSpecifierDeclaration,
  MemberDeclaration,
  ModifierDeclaration,
  ModuleDeclaration,
  NameEntity,
  ParameterDeclaration,
  ReferenceEntity,
  SourceFileDeclaration,
  StatementDeclaration,
  TypeDeclaration,
  TypeParameter,
  TypeParamReferenceDeclaration,
  TypeReferenceDeclaration
} from "./ast";
import {createLogger} from "../Logger";
import {
  ArrayDestructuringDeclarationProto, BindingElementDeclarationProto, BindingVariableDeclarationProto,
  BlockDeclarationProto,
  BreakStatementDeclarationProto,
  CallSignatureDeclarationProto,
  CaseDeclarationProto,
  ClassDeclarationProto,
  ConstructorDeclarationProto,
  ContinueStatementDeclarationProto,
  DefinitionInfoDeclarationProto,
  EnumDeclarationProto,
  EnumTokenDeclarationProto,
  ExportAssignmentDeclarationProto,
  ExpressionStatementDeclarationProto,
  ForOfStatementDeclarationProto,
  ForStatementDeclarationProto,
  FunctionDeclarationProto,
  HeritageClauseDeclarationProto,
  IdentifierDeclarationProto,
  IfStatementDeclarationProto,
  ImportClauseDeclarationProto,
  ImportEqualsDeclarationProto,
  ImportSpecifierDeclarationProto,
  IndexSignatureDeclarationProto,
  InterfaceDeclarationProto,
  IntersectionTypeDeclarationProto,
  MemberDeclarationProto,
  MethodSignatureDeclarationProto,
  ModifierDeclarationProto,
  ModuleDeclarationProto,
  NameDeclarationProto,
  NamedImportsDeclarationProto,
  NamespaceImportDeclarationProto,
  NumericLiteralDeclarationProto,
  ObjectLiteralDeclarationProto,
  ParameterDeclarationProto,
  ParameterValueDeclarationProto,
  PropertyDeclarationProto,
  QualifierDeclarationProto,
  ReferenceClauseDeclarationProto,
  ReferenceDeclarationProto,
  ReturnStatementDeclarationProto,
  SourceFileDeclarationProto,
  StatementDeclarationProto,
  StringLiteralDeclarationProto,
  SwitchStatementDeclarationProto,
  ThisTypeDeclarationProto,
  ThrowStatementDeclarationProto,
  TopLevelDeclarationProto,
  TupleDeclarationProto,
  TypeAliasDeclarationProto,
  TypeParameterDeclarationProto,
  TypeParamReferenceDeclarationProto,
  TypeReferenceDeclarationProto,
  UnionTypeDeclarationProto,
  VariableDeclarationProto,
  VariableLikeDeclarationProto,
  WhileStatementDeclarationProto
} from "declarations";
import {tsInternals} from "../TsInternals";
import * as ts from "../../.tsdeclarations/typescript";
import MODULE_KINDMap = ModuleDeclarationProto.MODULE_KINDMap;
import MODIFIER_KINDMap = ModifierDeclarationProto.MODIFIER_KINDMap;
import has = Reflect.has;

export class AstFactory {

  private log = createLogger("AstFactory");

  createNamespaceImportClause(name: string): ImportClauseDeclaration {
    let namespaceClause = new NamespaceImportDeclarationProto();
    namespaceClause.setName(name);
    let importClause = new ImportClauseDeclarationProto();
    importClause.setNamespaceimport(namespaceClause);
    return importClause;
  }

  createImportSpecifier(name: ts.Identifier, propertyName: ts.Identifier | null, uid: string | null): ImportSpecifierDeclaration {
    let importSpecifier = new ImportSpecifierDeclarationProto();
    importSpecifier.setName(name.getText());
    if (propertyName) {
      importSpecifier.setPropertyname(propertyName.getText());
    }
    if (uid) {
      importSpecifier.setUid(uid);
    }
    return importSpecifier;
  }

  createReferenceClause(path: string, referencedFile: string): ReferenceClauseDeclarationProto {
    let referenceClause = new ReferenceClauseDeclarationProto();
    referenceClause.setPath(path);
    referenceClause.setReferencedfile(referencedFile);
    return referenceClause;
  }

  createNamedImportsClause(importSpecifiers: Array<ImportSpecifierDeclaration>): ImportClauseDeclaration {
    let namedImportClause = new NamedImportsDeclarationProto();
    namedImportClause.setImportspecifiersList(importSpecifiers);
    let importClause = new ImportClauseDeclarationProto();
    importClause.setNamedimports(namedImportClause);
    return importClause;
  }

  createCallSignatureDeclaration(parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>): MemberDeclaration {
    let callSignature = new CallSignatureDeclarationProto();
    callSignature.setParametersList(parameters);
    callSignature.setType(type);
    callSignature.setTypeparametersList(typeParams);

    let memberProto = new MemberDeclarationProto();
    memberProto.setCallsignature(callSignature);
    return memberProto;
  }

  createClassDeclaration(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclaration>, modifiers: Array<ModifierDeclaration>, definitions: Array<DefinitionInfoDeclaration>, uid: string): ClassDeclaration {
    let classDeclaration = new ClassDeclarationProto();
    classDeclaration.setName(name);
    classDeclaration.setModifiersList(modifiers);
    classDeclaration.setUid(uid);
    classDeclaration.setMembersList(members);
    classDeclaration.setTypeparametersList(typeParams);
    classDeclaration.setParententitiesList(parentEntities);
    classDeclaration.setDefinitionsinfoList(definitions);
    return classDeclaration
  }

  createClassDeclarationAsTopLevel(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclaration>, modifiers: Array<ModifierDeclaration>, definitions: Array<DefinitionInfoDeclaration>, uid: string): Declaration {
    let classDeclaration = this.createClassDeclaration(name, members, typeParams, parentEntities, modifiers, definitions, uid);

    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setClassdeclaration(classDeclaration);
    return topLevelDeclaration;
  }

  createConstructorDeclaration(parameters: Array<ParameterDeclaration>, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null): MemberDeclaration {
    let constructorDeclaration = new ConstructorDeclarationProto();

    constructorDeclaration.setParametersList(parameters);
    constructorDeclaration.setTypeparametersList(typeParams);
    constructorDeclaration.setModifiersList(modifiers);
    if (body) {
      constructorDeclaration.setBody(body)
    }

    let memberProto = new MemberDeclarationProto();
    memberProto.setConstructordeclaration(constructorDeclaration);
    return memberProto;
  }

  createDefinitionInfoDeclaration(uid: string, fileName: string): DefinitionInfoDeclaration {
    let definition = new DefinitionInfoDeclarationProto();
    definition.setUid(uid);
    definition.setFilename(tsInternals.normalizePath(fileName));
    return definition;
  }

  createEnumDeclaration(name: string, values: Array<EnumTokenDeclaration>, uid: string): Declaration {
    let enumDeclaration = new EnumDeclarationProto();
    enumDeclaration.setName(name);
    enumDeclaration.setValuesList(values);
    enumDeclaration.setUid(uid);

    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setEnumdeclaration(enumDeclaration);
    return topLevelDeclaration;
  }

  createEnumTokenDeclaration(value: string, meta: string): EnumTokenDeclaration {
    let enumToken = new EnumTokenDeclarationProto();
    enumToken.setValue(value);
    enumToken.setMeta(meta);
    return enumToken;
  }

  createExportAssignmentDeclaration(uids: Array<string>, isExportEquals: boolean): Declaration {
    let exportAssignment = new ExportAssignmentDeclarationProto();
    exportAssignment.setUidList(uids);
    exportAssignment.setIsexportequals(isExportEquals);

    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setExportassignment(exportAssignment);
    return topLevelDeclaration;
  }

  createExpressionStatement(expression: Expression): StatementDeclaration {
    let expressionStatement = new ExpressionStatementDeclarationProto();
    expressionStatement.setExpression(expression);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setExpressionstatement(expressionStatement);
    return statementDeclaration;
  }

  createIfStatement(condition: Expression, thenStatement: Array<StatementDeclaration>, elseStatement: Array<StatementDeclaration> | null): StatementDeclaration {
    let ifStatement = new IfStatementDeclarationProto();
    ifStatement.setCondition(condition);
    ifStatement.setThenstatementList(thenStatement);
    if (elseStatement) {
      ifStatement.setElsestatementList(elseStatement);
    }

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setIfstatement(ifStatement);
    return statementDeclaration;
  }

  createForStatement(initializer: Array<StatementDeclaration>, condition: Expression | null, incrementor: Expression | null, statement: Array<StatementDeclaration>): StatementDeclaration {
    let forStatement = new ForStatementDeclarationProto();
    forStatement.setInitializerList(initializer);
    if (condition) {
      forStatement.setCondition(condition);
    }
    if (incrementor) {
      forStatement.setIncrementor(incrementor);
    }
    forStatement.setStatementList(statement);
    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setForstatement(forStatement);
    return statementDeclaration;
  }

  createForOfStatement(initializer: Array<StatementDeclaration>, expression: Expression, statement: Array<StatementDeclaration>): StatementDeclaration {
    let forOfStatement = new ForOfStatementDeclarationProto();
    forOfStatement.setVariable(initializer[0]);
    forOfStatement.setExpression(expression)
    forOfStatement.setStatementList(statement);
    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setForofstatement(forOfStatement);
    return statementDeclaration;
  }

  createWhileStatement(condition: Expression, statement: Array<StatementDeclaration>): StatementDeclaration {
    let whileStatement = new WhileStatementDeclarationProto();
    whileStatement.setCondition(condition);
    whileStatement.setStatementList(statement);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setWhilestatement(whileStatement);
    return statementDeclaration;
  }

  createReturnStatement(expression: Expression | null): StatementDeclaration {
    let returnStatement = new ReturnStatementDeclarationProto();
    if (expression) {
      returnStatement.setExpression(expression);
    }

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setReturnstatement(returnStatement);
    return statementDeclaration;
  }

  createBreakStatement(): StatementDeclaration {
    let breakStatement = new BreakStatementDeclarationProto();

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setBreakstatement(breakStatement);
    return statementDeclaration;
  }

  createContinueStatement(): StatementDeclaration {
    let continueStatement = new ContinueStatementDeclarationProto();

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setContinuestatement(continueStatement);
    return statementDeclaration;
  }

  createThrowStatement(expression: Expression): StatementDeclaration {
    let throwStatement = new ThrowStatementDeclarationProto();
    throwStatement.setExpression(expression);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setThrowstatement(throwStatement);
    return statementDeclaration;
  }

  createCaseDeclaration(condition: Expression | null, body: Array<StatementDeclaration>): CaseDeclarationProto {
    let caseDeclaration = new CaseDeclarationProto();
    if (condition) {
      caseDeclaration.setCondition(condition);
    }
    caseDeclaration.setStatementList(body);
    return caseDeclaration;
  }

  createSwitchStatement(expression: Expression, cases: Array<CaseDeclarationProto>): StatementDeclaration {
    let switchStatement = new SwitchStatementDeclarationProto();
    switchStatement.setExpression(expression);
    switchStatement.setCaseList(cases);
    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setSwitchstatement(switchStatement);
    return statementDeclaration;
  }

  createBlockDeclaration(statements: Array<StatementDeclaration>): Block {
    let block = new BlockDeclarationProto();
    block.setStatementsList(statements);
    return block
  }

  createBlockStatementDeclaration(statements: Array<StatementDeclaration>): StatementDeclaration {
    let block = this.createBlockDeclaration(statements);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setBlockstatement(block);
    return statementDeclaration;
  }

  createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, isGenerator: boolean): FunctionDeclaration {
    let functionDeclaration = new FunctionDeclarationProto();
    functionDeclaration.setName(name);
    functionDeclaration.setParametersList(parameters);
    functionDeclaration.setType(type);
    functionDeclaration.setTypeparametersList(typeParams);
    functionDeclaration.setModifiersList(modifiers);
    if (body) {
      functionDeclaration.setBody(body);
    }
    functionDeclaration.setUid(uid);
    functionDeclaration.setDefinitionsinfoList(definitionsInfo);
    functionDeclaration.setIsgenerator(isGenerator)
    return functionDeclaration
  }

  createFunctionDeclarationAsMember(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null, uid: string, isGenerator: boolean): MemberDeclaration {
    let functionDeclaration = this.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, body, [], uid, isGenerator);

    let memberProto = new MemberDeclarationProto();
    memberProto.setFunctiondeclaration(functionDeclaration);
    return memberProto;
  }

  createFunctionDeclarationAsTopLevel(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string, isGenerator: boolean): StatementDeclaration {
    let functionDeclaration = this.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, body, definitionsInfo, uid, isGenerator);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setFunctiondeclaration(functionDeclaration);
    return statementDeclaration;
  }

  createFunctionTypeDeclaration(parameters: Array<ParameterDeclaration>, type: TypeDeclaration): TypeDeclaration {
    let functionType = new FunctionDeclarationProto();
    functionType.setParametersList(parameters);
    functionType.setType(type);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setFunctiontypedeclaration(functionType);
    return paramValueDeclaration;
  }

  createHeritageClauseDeclaration(name: NameEntity, typeArguments: Array<TypeDeclaration>, extending: boolean, typeReference: ReferenceEntity | null): HeritageClauseDeclaration {
    let heritageClauseDeclaration = new HeritageClauseDeclarationProto();

    heritageClauseDeclaration.setName(name);
    heritageClauseDeclaration.setTypeargumentsList(typeArguments);
    heritageClauseDeclaration.setExtending(extending);

    if (typeReference != null) {
      heritageClauseDeclaration.setTypereference(typeReference);
    }

    return heritageClauseDeclaration;
  }

  createIdentifierDeclarationAsNameEntity(value: string): NameEntity {
    let identifierProto = this.createIdentifierDeclaration(value);

    let nameDeclaration = new NameDeclarationProto();
    nameDeclaration.setIdentifier(identifierProto);
    return nameDeclaration;
  }

  createIdentifierDeclaration(value: string): IdentifierDeclaration {
    let identifierProto = new IdentifierDeclarationProto();
    identifierProto.setValue(value);
    return identifierProto;
  }

  createImportEqualsDeclaration(name: string, moduleReference: NameEntity, uid: string): Declaration {
    let importEqualsDeclaration = new ImportEqualsDeclarationProto();
    importEqualsDeclaration.setName(name);
    importEqualsDeclaration.setModulereference(moduleReference);
    importEqualsDeclaration.setUid(uid);

    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setImportequals(importEqualsDeclaration);
    return topLevelDeclaration;
  }

  createIndexSignatureDeclaration(indexTypes: Array<ParameterDeclaration>, returnType: TypeDeclaration): MemberDeclaration {
    let indexSignatureDeclaration = new IndexSignatureDeclarationProto();
    indexSignatureDeclaration.setIndextypesList(indexTypes);
    indexSignatureDeclaration.setReturntype(returnType);

    let memberEntity = new MemberDeclarationProto();
    memberEntity.setIndexsignature(indexSignatureDeclaration);
    return memberEntity;
  }

  createInterfaceDeclaration(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclaration>, modifiers: Array<ModifierDeclaration>, definitionsInfo: Array<DefinitionInfoDeclaration>, uid: string): Declaration {
    let interfaceDeclaration = new InterfaceDeclarationProto();
    interfaceDeclaration.setName(name);
    interfaceDeclaration.setUid(uid);
    interfaceDeclaration.setDefinitionsinfoList(definitionsInfo);
    interfaceDeclaration.setMembersList(members);
    interfaceDeclaration.setTypeparametersList(typeParams);
    interfaceDeclaration.setParententitiesList(parentEntities);
    interfaceDeclaration.setModifiersList(modifiers);

    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setInterfacedeclaration(interfaceDeclaration);
    return topLevelDeclaration;
  }

  createIntersectionTypeDeclaration(params: Array<TypeDeclaration>): TypeDeclaration {
    let intersection = new IntersectionTypeDeclarationProto();
    intersection.setParamsList(params);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setIntersectiontype(intersection);
    return paramValueDeclaration;
  }

  createMethodDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>): MemberDeclaration {
    let methodDeclaration = new FunctionDeclarationProto();
    methodDeclaration.setName(name);
    methodDeclaration.setParametersList(parameters);
    methodDeclaration.setType(type);
    methodDeclaration.setTypeparametersList(typeParams);

    let memberProto = new MemberDeclarationProto();
    memberProto.setFunctiondeclaration(methodDeclaration);
    return memberProto;
  }

  createMethodSignatureDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>): MemberDeclaration {
    let methodSignature = new MethodSignatureDeclarationProto();
    methodSignature.setName(name);
    methodSignature.setParametersList(parameters);
    methodSignature.setType(type);
    methodSignature.setTypeparametersList(typeParams);
    methodSignature.setOptional(optional);
    methodSignature.setModifiersList(modifiers);

    let memberProto = new MemberDeclarationProto();
    memberProto.setMethodsignature(methodSignature);
    return memberProto;
  }

  createModifierDeclaration(name: MODIFIER_KINDMap[keyof MODIFIER_KINDMap]): ModifierDeclaration {
    let modifierDeclaration = new ModifierDeclarationProto();
    modifierDeclaration.setToken(name);
    return modifierDeclaration;
  }

  createModuleDeclaration(
    packageName: NameEntity | null,
    imports: Array<ImportClauseDeclaration>,
    references: Array<ReferenceClauseDeclarationProto>,
    moduleDeclarations: Iterable<Declaration>,
    modifiers: Array<ModifierDeclaration>,
    uid: string,
    resourceName: string,
    definitions: Array<DefinitionInfoDeclaration>,
    kind: MODULE_KINDMap[keyof MODULE_KINDMap],
    isLib: boolean
  ): ModuleDeclaration {
    let moduleDeclaration = new ModuleDeclarationProto();

    moduleDeclaration.setImportsList(imports);
    moduleDeclaration.setReferencesList(references);

    if (packageName) {
      moduleDeclaration.setPackagename(packageName);
    }

    moduleDeclaration.setDeclarationsList(Array.from(moduleDeclarations));
    moduleDeclaration.setModifiersList(modifiers);

    moduleDeclaration.setUid(uid);
    moduleDeclaration.setResourcename(resourceName);
    moduleDeclaration.setDefinitionsinfoList(definitions);
    moduleDeclaration.setKind(kind);

    moduleDeclaration.setIslibsource(isLib);
    return moduleDeclaration;
  }

  createModuleDeclarationAsTopLevel(module: ModuleDeclaration): TopLevelDeclarationProto {
    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setModuledeclaration(module);
    return topLevelDeclaration;
  }

  createObjectLiteral(members: Array<MemberDeclaration>, uid: string): TypeDeclaration {
    let objectLiteral = new ObjectLiteralDeclarationProto();
    objectLiteral.setMembersList(members);
    objectLiteral.setUid(uid);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setObjectliteral(objectLiteral);
    return paramValueDeclaration;
  }

  createParameterDeclaration(name: string, type: TypeDeclaration, initializer: Expression | null, vararg: boolean, optional: boolean, hasType: boolean): ParameterDeclaration {
    let parameterDeclaration = new ParameterDeclarationProto();
    parameterDeclaration.setName(name);
    parameterDeclaration.setType(type);
    if (initializer != null) {
      parameterDeclaration.setInitializer(initializer);
    }
    parameterDeclaration.setVararg(vararg);
    parameterDeclaration.setOptional(optional);
    parameterDeclaration.setHastype(hasType);
    return parameterDeclaration;
  }

  createQualifiedNameEntity(left: NameEntity, right: IdentifierDeclaration): NameEntity {
    let qualifier = new QualifierDeclarationProto();
    qualifier.setLeft(left);
    qualifier.setRight(right);

    let nameDeclaration = new NameDeclarationProto();
    nameDeclaration.setQualifier(qualifier);
    return nameDeclaration;
  }

  createReferenceEntity<T extends Declaration>(
    uid: string,
    origin: ReferenceDeclarationProto.ORIGINMap[keyof ReferenceDeclarationProto.ORIGINMap],
    kind: ReferenceDeclarationProto.KINDMap[keyof ReferenceDeclarationProto.KINDMap]
  ): ReferenceEntity {
    let reference = new ReferenceDeclarationProto();
    reference.setUid(uid);
    reference.setOrigin(origin);
    reference.setKind(kind);
    return reference;
  }

  createSourceFileDeclaration(fileName: string, root: ModuleDeclaration | null): SourceFileDeclaration {
    let sourceFile = new SourceFileDeclarationProto();
    sourceFile.setFilename(tsInternals.normalizePath(fileName));
    if (root) {
      sourceFile.setRoot(root);
    }
    return sourceFile;
  }

  createStatementAsTopLevel(statement: StatementDeclaration): Declaration {
    let topLevelStatement = new TopLevelDeclarationProto();
    topLevelStatement.setStatement(statement);
    return topLevelStatement;
  }

  createNumericLiteralDeclaration(token: string): TypeDeclaration {
    let numericLiteral = new NumericLiteralDeclarationProto();
    numericLiteral.setToken(token);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setNumericliteral(numericLiteral);
    return paramValueDeclaration;
  }

  createStringLiteralDeclaration(token: string): TypeDeclaration {
    let stringLiteral = new StringLiteralDeclarationProto();
    stringLiteral.setToken(token);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setStringliteral(stringLiteral);
    return paramValueDeclaration;
  }

  createThisTypeDeclaration(): TypeDeclaration {
    let parameterValueDeclaration = new ParameterValueDeclarationProto();
    parameterValueDeclaration.setThistype(new ThisTypeDeclarationProto());
    return parameterValueDeclaration;
  }

  createTupleDeclaration(params: Array<TypeDeclaration>): TypeDeclaration {
    let tupleDeclaration = new TupleDeclarationProto();
    tupleDeclaration.setParamsList(params);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setTupledeclaration(tupleDeclaration);
    return paramValueDeclaration;
  }

  createTypeAliasDeclaration(aliasName: NameEntity, typeParams: Array<TypeParameterDeclarationProto>, typeReference: TypeDeclaration, uid: string): Declaration {
    let typeAlias = new TypeAliasDeclarationProto();
    typeAlias.setAliasname(aliasName);
    typeAlias.setTypeparametersList(typeParams);
    typeAlias.setTypereference(typeReference);
    typeAlias.setUid(uid);

    let topLevelDeclaration = new TopLevelDeclarationProto();
    topLevelDeclaration.setAliasdeclaration(typeAlias);
    return topLevelDeclaration;
  }

  createTypeReferenceDeclaration(value: NameEntity, params: Array<TypeDeclaration>, typeReference: ReferenceEntity | null = null): TypeReferenceDeclaration {
    let typeDeclaration = new TypeReferenceDeclarationProto();
    typeDeclaration.setValue(value);
    typeDeclaration.setParamsList(params);
    if (typeReference != null) {
      this.log.trace(`type reference for ${value} ${typeReference}`);
      typeDeclaration.setTypereference(typeReference);
    }

    return typeDeclaration;
  }

  createTypeParamReferenceDeclaration(value: NameEntity): TypeParamReferenceDeclaration {
    let typeDeclaration = new TypeParamReferenceDeclarationProto();
    typeDeclaration.setValue(value);
    return typeDeclaration;
  }

  createTypeParamReferenceDeclarationAsParamValue(value: NameEntity) {
    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setTypeparamreferencedeclaration(this.createTypeParamReferenceDeclaration(value));
    return paramValueDeclaration;
  }

  createTypeReferenceDeclarationAsParamValue(value: NameEntity, params: Array<TypeDeclaration>, typeReference: ReferenceEntity | null): TypeDeclaration {
    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setTypereferencedeclaration(this.createTypeReferenceDeclaration(value, params, typeReference));
    return paramValueDeclaration;
  }

  createTypeParam(name: NameEntity, constraints: Array<TypeDeclaration>, defaultValue: TypeDeclaration | null): TypeParameter {
    let typeParam = new TypeParameterDeclarationProto();
    typeParam.setName(name);
    typeParam.setConstraintsList(constraints);
    if (defaultValue) {
      typeParam.setDefaultvalue(defaultValue);
    }
    return typeParam;
  }

  createUnionTypeDeclaration(params: Array<TypeDeclaration>): TypeDeclaration {
    let unionTypeDeclaration = new UnionTypeDeclarationProto();
    unionTypeDeclaration.setParamsList(params);

    let paramValueDeclaration = new ParameterValueDeclarationProto();
    paramValueDeclaration.setUniontype(unionTypeDeclaration);
    return paramValueDeclaration;
  }

  declareProperty(name: string, initializer: Expression | null, type: TypeDeclaration, typeParams: Array<TypeParameter>, optional: boolean, modifiers: Array<ModifierDeclaration>, hasType: boolean): MemberDeclaration {
    let propertyDeclaration = new PropertyDeclarationProto();
    propertyDeclaration.setName(name);
    if (initializer) {
      propertyDeclaration.setInitializer(initializer);
    }
    propertyDeclaration.setType(type);
    propertyDeclaration.setTypeparametersList(typeParams);
    propertyDeclaration.setOptional(optional);
    propertyDeclaration.setModifiersList(modifiers);
    propertyDeclaration.setHastype(hasType);

    let memberProto = new MemberDeclarationProto();
    memberProto.setProperty(propertyDeclaration);
    return memberProto;
  }

  declareVariable(name: string, type: TypeDeclaration, modifiers: Array<ModifierDeclaration>, initializer: Expression | null, definitions: Array<DefinitionInfoDeclaration>, uid: string, hasType: boolean): StatementDeclaration {
    let variableDeclaration = new VariableDeclarationProto();
    variableDeclaration.setName(name);
    variableDeclaration.setType(type);
    variableDeclaration.setModifiersList(modifiers);
    if (initializer) {
      variableDeclaration.setInitializer(initializer);
    }
    variableDeclaration.setUid(uid);
    variableDeclaration.setDefinitionsinfoList(definitions);
    variableDeclaration.setHastype(hasType);

    let variableLikeDeclaration = new VariableLikeDeclarationProto();
    variableLikeDeclaration.setVariable(variableDeclaration);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setVariablelikedeclaration(variableLikeDeclaration);
    return statementDeclaration;
  }

  createBindingVariableDeclaration(name: string, expression: Expression | null): BindingElementDeclaration {
    let bindingVariableDeclaration = new BindingVariableDeclarationProto();
    bindingVariableDeclaration.setName(name);
    if (expression) {
      bindingVariableDeclaration.setExpression(expression);
    }

    let bindingElementDeclaration = new BindingElementDeclarationProto();
    bindingElementDeclaration.setBindingvariable(bindingVariableDeclaration);
    return bindingElementDeclaration;
  }

  declareArrayBindingPatternAsBindingElement(elements: Array<BindingElementDeclaration>): BindingElementDeclaration {
    let arrayDestructuringDeclaration = new ArrayDestructuringDeclarationProto();
    arrayDestructuringDeclaration.setElementsList(elements);

    let bindingElementDeclaration = new BindingElementDeclarationProto();
    bindingElementDeclaration.setArraydestructuring(arrayDestructuringDeclaration);

    return bindingElementDeclaration;
  }

  declareArrayBindingPatternAsStatement(elements: Array<BindingElementDeclaration>): StatementDeclaration {

    let arrayDestructuringDeclaration = new ArrayDestructuringDeclarationProto();
    arrayDestructuringDeclaration.setElementsList(elements);

    let variableLikeDeclaration = new VariableLikeDeclarationProto();
    variableLikeDeclaration.setArraydestructuring(arrayDestructuringDeclaration);

    let statementDeclaration = new StatementDeclarationProto();
    statementDeclaration.setVariablelikedeclaration(variableLikeDeclaration);
    return statementDeclaration;
  }

}