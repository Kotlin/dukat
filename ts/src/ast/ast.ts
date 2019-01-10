declare interface AstNode {
}


declare interface Declaration extends AstNode {
}

declare class VariableDeclaration implements Declaration {
    name: String;
    type: TypeDeclaration;
}

declare class Expression implements Declaration {
    kind: TypeDeclaration;
    meta: String;
}

declare interface ParameterValue extends Declaration {}

declare class ParameterDeclaration {
  name: String;
  type: ParameterValue;
}

declare class DocumentRoot implements AstNode {
    declarations: Declaration[]
}

declare class AstTree {
    root: DocumentRoot
}


declare class TypeDeclaration implements ParameterValue {
    constructor(value: string, params: Array<ParameterValue>);
}

declare class FunctionDeclaration {
}

declare class FunctionTypeDeclaration implements ParameterValue {}
