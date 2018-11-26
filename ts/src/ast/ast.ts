
declare interface AstNode {
}


declare interface Declaration extends AstNode {}

declare class VariableDeclaration implements Declaration {
  name: String;
  type: TypeDeclaration;
}

declare class ParameterDeclaration implements Declaration {
  name: String;
}

declare class DocumentRoot implements AstNode {
  declarations: Declaration[]
}

declare class AstTree {
  root: DocumentRoot
}


declare interface TypeDeclaration {}

declare class SimpleTypeDeclaration implements TypeDeclaration {
  constructor(value: string)
}

declare class FunctionDeclaration {}

declare class AstFactory {
  declareVariable(value: string, type: TypeDeclaration): VariableDeclaration;
  createParameterDeclaration(name: string, type: TypeDeclaration): ParameterDeclaration;
  createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>): FunctionDeclaration;
  createTypeDeclaration(value: string): SimpleTypeDeclaration;
  createGenericTypeDeclaration(value: string, params: Array<SimpleTypeDeclaration>): SimpleTypeDeclaration;
  createAstTree(declarations: Declaration[]): AstTree;
}