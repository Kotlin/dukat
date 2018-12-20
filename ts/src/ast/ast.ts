
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


declare class TypeDeclaration {
  constructor(value: string)
}

declare class FunctionDeclaration {}

declare class AstFactory {
  declareVariable(value: string, type: TypeDeclaration): VariableDeclaration;
  createParameterDeclaration(name: string, type: TypeDeclaration): ParameterDeclaration;
  createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): FunctionDeclaration;
  createTypeDeclaration(value: string): TypeDeclaration;
  createGenericTypeDeclaration(value: string, params: Array<TypeDeclaration>): TypeDeclaration;
  createDocumentRoot(declarations: Declaration[]): DocumentRoot;
}