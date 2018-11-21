
declare class AstNode {}

declare class VariableDeclaration {
  name: String;
  type: TypeDeclaration;
}



declare class DocumentRoot extends AstNode {
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
  createSimpleTypeDeclaration(value: string): SimpleTypeDeclaration;
  createGenericTypeDeclaration(value: string, params: Array<SimpleTypeDeclaration>): SimpleTypeDeclaration;
}