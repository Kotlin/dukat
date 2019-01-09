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

declare class ParameterDeclaration implements Declaration {
    name: String;
    type: TypeDeclaration
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

declare class FunctionDeclaration {
}

declare interface AstFactory {
    createExpression(kind: TypeDeclaration, meta: String): Expression;

    declareVariable(value: string, type: TypeDeclaration): VariableDeclaration;

    createParameterDeclaration(name: string, type: TypeDeclaration, initializer: Expression | null): ParameterDeclaration;

    createFunctionDeclaration(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration): FunctionDeclaration;

    createTypeDeclaration(value: string, params: Array<TypeDeclaration>): TypeDeclaration;

    createDocumentRoot(declarations: Declaration[]): DocumentRoot;
}