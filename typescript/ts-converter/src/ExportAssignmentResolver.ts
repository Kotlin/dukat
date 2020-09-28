import * as ts from "typescript";

export class ExportAssignmentResolver {
  constructor(private typeChecker: ts.TypeChecker) {}

  private declarationMap = new Map<ts.Node, ts.ExportAssignment>()

  private register(exportAssignment: ts.ExportAssignment) {
    let expression = exportAssignment.expression;
    let symbol = this.typeChecker.getSymbolAtLocation(expression);

    if (symbol) {
      if (symbol.flags & ts.SymbolFlags.Alias) {
        symbol = this.typeChecker.getAliasedSymbol(symbol);
      }

      if (symbol && Array.isArray(symbol.declarations) && symbol.declarations.length > 0) {
        for (let declaration of symbol.declarations) {
          this.declarationMap.set(declaration, exportAssignment)
        }
      }
    }
  }

  visit(node: ts.Node) {
    if (ts.isExportAssignment(node)) {
      this.register(node);
    }
    ts.forEachChild(node, node => this.visit(node))
  }

  resolveStatement(statement: ts.Node):  ts.ExportAssignment | undefined {
    return this.declarationMap.get(statement);
  }
}