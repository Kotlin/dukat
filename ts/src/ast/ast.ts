
declare interface AstNode {
}

declare class VariableDeclaration {
  name: String;
  type: TypeDeclaration;
}



declare class DocumentRoot implements AstNode {
  declarations: VariableDeclaration[]
}

declare class AstTree {
  root: DocumentRoot
}


declare interface TypeDeclaration {}

declare class SimpleTypeDeclaration implements TypeDeclaration {
  constructor(value: string)
}

declare class AstFactory {
  declareVariable(value: string, type: TypeDeclaration): VariableDeclaration;
  createTypeDeclaration(value: string): SimpleTypeDeclaration;
  createGenericTypeDeclaration(value: string, params: Array<SimpleTypeDeclaration>): SimpleTypeDeclaration;
}