import * as ts from "typescript";

export interface AstVisitor {
  visitType(type: ts.TypeNode): void;
}