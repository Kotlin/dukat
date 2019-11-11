export declare interface ProtoMessage {
    serializeBinary(): Int8Array;
}

export declare interface NameEntityProto extends ProtoMessage {}
export declare interface IdentifierEntityProto extends NameEntityProto {
    setValue(value: string): void;
}
export declare interface QualifierEntityProto extends NameEntityProto {
    setRight(right: IdentifierEntityProto): void;
    setLeft(left: NameEntityProto): void;
}

declare interface AstNode {
}


export declare class Declaration implements AstNode, ProtoMessage {
    serializeBinary(): Int8Array
}

export declare interface ReferenceEntity {
    uid: String;
}

export declare interface DefinitionInfoDeclaration extends AstNode {
    fileName: string;
}

export declare interface TupleDeclaration extends ParameterValue {}

export declare interface NameEntity extends Declaration {
    hasIdentifier(): boolean;
    hasQualifier(): boolean;
    getQualifier(): QualifierEntity;
    getIdentifier(): IdentifierEntity;
}

export declare interface IdentifierEntity extends ParameterValue, ModuleReferenceDeclaration, NameEntity {}
export declare interface QualifierEntity extends NameEntity {
    getLeft(): NameEntity,
    getRight(): IdentifierEntity
}

export declare interface ThisTypeDeclaration extends Declaration {}
export declare interface ModuleReferenceDeclaration extends Declaration {}

export declare interface ImportEqualsDeclaration extends Declaration {
    name: string
}

export declare interface EnumDeclaration extends ParameterValue {
    values: Array<EnumTokenDeclaration>
}

export declare interface EnumTokenDeclaration extends Declaration {
    value: string;
    meta: string;
}

export declare interface ExportAssignmentDeclaration extends Declaration {
    name: string;
}

export declare interface IntersectionTypeDeclaration extends Declaration {}
export declare interface UnionTypeDeclatation extends Declaration {}
export declare interface HeritageClauseDeclaration extends Declaration {
    name: NameEntity,
    typeArguments: Array<ParameterValue>,
    extending: boolean,
    typeReference: ReferenceEntity | null
}
export declare interface TypeAliasDeclaration extends Declaration {}
export declare interface IndexSignatureDeclaration extends ParameterValue {}
export declare interface StringLiteralDeclaration
    extends ParameterValue {}

export declare interface CallSignatureDeclaration extends MemberDeclaration {}
export declare interface ConstructorDeclaration extends MemberDeclaration {}

export declare interface ModifierDeclaration extends Declaration {
    token: string;
}

export declare interface MemberDeclaration extends Declaration {
}

export declare class ClassDeclaration implements Declaration {
    name: String;
    serializeBinary(): Int8Array;
}

export declare class InterfaceDeclaration implements Declaration {
    serializeBinary(): Int8Array;
}
export declare class MethodSignatureDeclaration implements Declaration {
    serializeBinary(): Int8Array;
}

export declare class ObjectLiteral implements ParameterValue {
    serializeBinary(): Int8Array;
}

export declare class VariableDeclaration implements Declaration {
    name: String;
    type: TypeDeclaration;
    serializeBinary(): Int8Array;
}

export declare class PropertyDeclaration implements MemberDeclaration {
    name: string;
    type: TypeDeclaration;
    immmutable: boolean;
    serializeBinary(): Int8Array;
}

export declare interface LiteralExpression extends Expression {}

export declare interface Expression extends Declaration {}

export declare class ExpressionStatement implements Declaration {
    expression: Expression;
    serializeBinary(): Int8Array;
}

export declare interface ParameterValue extends Declaration {}

export declare class ParameterDeclaration extends Declaration {
  name: String;
  type: ParameterValue;
}

export declare class ModuleDeclaration implements Declaration {
    declarations: Declaration[];
    serializeBinary(): Int8Array;
}

export declare class SourceFileDeclaration implements AstNode {
    declarations: Declaration[]
}

export declare class SourceSet implements Declaration {
  serializeBinary(): Int8Array;
}

export declare class SourceBundle implements Declaration {
    serializeBinary(): Int8Array;
}

export declare class TypeParameter {
    name: String;
}

export type ClassLikeDeclaration = ClassDeclaration | InterfaceDeclaration

export declare class TypeDeclaration implements ParameterValue {
    serializeBinary(): Int8Array;
}

export declare class Block implements Declaration {
    statements: Array<Declaration>
    serializeBinary(): Int8Array;
}

export declare class FunctionDeclaration implements MemberDeclaration {
    serializeBinary(): Int8Array;
}

export declare class FunctionTypeDeclaration implements ParameterValue {
    serializeBinary(): Int8Array;
}
