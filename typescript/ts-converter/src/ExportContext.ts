import {uid} from "./uid";
import * as ts from "typescript";
import {createLogger} from "./Logger";

function resolveName(node: ts.Node): string | null {
  if (ts.isIdentifier(node)) {
    return node.text;
  }

  if (node.name && (ts.isIdentifier(node.name) || ts.isStringLiteral(node.name))) {
    return node.name.text;
  }

  return null;
}

export function resolveDeclarations(node: ts.Node, typeChecker: ts.TypeChecker): Array<ts.Node> {
  let symbolAtLocation = typeChecker.getSymbolAtLocation(node);
  if (symbolAtLocation) {

    if (symbolAtLocation.flags & ts.SymbolFlags.Alias) {
      let aliasedSymbol = typeChecker.getAliasedSymbol(symbolAtLocation);
      if (aliasedSymbol && Array.isArray(aliasedSymbol.declarations)) {
        return aliasedSymbol.declarations;
      } else {
        return [];
      }
    }

    if (Array.isArray(symbolAtLocation.declarations)) {
      return symbolAtLocation.declarations;
    } else {
      let declaredTyped = typeChecker.getDeclaredTypeOfSymbol(symbolAtLocation);
      if (declaredTyped) {
        let resolvedASymbol = declaredTyped.symbol || declaredTyped.aliasSymbol;
        if (resolvedASymbol && Array.isArray(resolvedASymbol.declarations)) {
          return resolvedASymbol.declarations;
        }
      }
    }
  }

  let symbol = typeChecker.getTypeAtLocation(node).symbol;
  if (symbol && Array.isArray(symbol.declarations)) {
    return symbol.declarations;
  }

  return [];
}

export class ExportContext {
  private exportTable: Map<ts.Node, string> = new Map();
  private log = createLogger("ExportContext");

  getUID(node: ts.Node): string {
    if (!this.exportTable.has(node)) {
      let nodeUid = uid();

      let name = resolveName(node);
      if (name) {
        nodeUid = nodeUid + `-${ts.SyntaxKind[node.kind]}-${name}`;
      }

      this.exportTable.set(node, nodeUid);
    }
    return this.exportTable.get(node) || "";
  }
}