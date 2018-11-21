
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
  constructor(value: number);
}


declare class AstFactory {
  createSimpleTypeDeclaration(value: number): SimpleTypeDeclaration;
}