import * as ts from "typescript";

type MergeableDeclaration = ts.FunctionDeclaration | ts.InterfaceDeclaration | ts.ClassDeclaration | ts.VariableDeclaration | ts.ModuleDeclaration;

export class DeclarationResolver {

  constructor(private program: ts.Program) {
  }

  resolve(node: MergeableDeclaration): ReadonlyArray<ts.Node> {
    let typeChecker = this.program.getTypeChecker();
    let symbol = typeChecker.getSymbolAtLocation(node.name);

    if (symbol && Array.isArray(symbol.declarations)) {
      return symbol.declarations.filter(it => (ts.isFunctionDeclaration(it) || ts.isInterfaceDeclaration(it) || ts.isClassDeclaration(it) || ts.isVariableDeclaration(it)  || ts.isModuleDeclaration(it)));
    }

    return [];
  }
}
