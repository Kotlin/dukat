
declare interface AstNode {
}


declare interface Declaration extends AstNode {}

declare interface QualifierDeclaration extends ModuleReferenceDeclaration, ParameterValue {
    left: ParameterValue,
    right: ParameterValue
}

declare interface IdentifierDeclaration extends ParameterValue, ModuleReferenceDeclaration {}
declare interface ThisTypeDeclaration extends Declaration {}
declare interface ModuleReferenceDeclaration extends Declaration {}

declare interface ImportEqualsDeclaration extends Declaration {
    name: string
}

declare interface EnumDeclaration extends ParameterValue {
    values: Array<EnumTokenDeclaration>
}

declare interface EnumTokenDeclaration extends Declaration {
    value: string;
    meta: string;
}

declare interface ExportAssignmentDeclaration extends Declaration {
    name: string;
}

declare interface TokenDeclaration extends Declaration {
    value: string
}
declare interface IntersectionTypeDeclaration extends Declaration {}
declare interface UnionTypeDeclatation extends Declaration {}
declare interface HeritageClauseDeclaration extends Declaration {}
declare interface TypeAliasDeclaration extends Declaration {}
declare interface IndexSignatureDeclaration extends ParameterValue {}
declare interface StringTypeDeclaration
    extends ParameterValue {}

declare interface CallSignatureDeclaration extends MemberDeclaration {}
declare interface ConstructorDeclaration extends MemberDeclaration {}

declare interface ModifierDeclaration extends Declaration {
    token: string;
}

declare interface MemberDeclaration extends Declaration {
}

declare class ClassDeclaration implements Declaration {
    name: String;
}

declare class InterfaceDeclaration implements Declaration {}
declare class MethodSignatureDeclaration implements Declaration {}

declare class ObjectLiteral implements ParameterValue {}

declare class VariableDeclaration implements Declaration {
    name: String;
    type: TypeDeclaration;
}

declare class PropertyDeclaration implements MemberDeclaration {
    name: string;
    type: TypeDeclaration;
    immmutable: boolean;
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
    root: DocumentRoot;
}


declare class TypeParameter {
    name: String;
}

type ClassLikeDeclaration = ClassDeclaration | InterfaceDeclaration

declare class TypeDeclaration implements ParameterValue {
    constructor(value: string, params: Array<ParameterValue>);
}

declare class FunctionDeclaration implements MemberDeclaration {}

declare class FunctionTypeDeclaration implements ParameterValue {}
