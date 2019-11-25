import * as ts from "typescript-services-api";

export interface AstVisitor {
  visitType(type: ts.TypeNode): void;
}