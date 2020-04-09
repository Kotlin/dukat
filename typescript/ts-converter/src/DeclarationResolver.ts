import * as ts from "typescript";


export class DeclarationResolver {

  constructor(private program: ts.Program) {
  }

  resolve(node: ts.InterfaceDeclaration | ts.ClassDeclaration | ts.VariableDeclaration | ts.ModuleDeclaration): ReadonlyArray<ts.Node> {
    let typeChecker = this.program.getTypeChecker();
    let symbol = typeChecker.getSymbolAtLocation(node.name);

    if (symbol && Array.isArray(symbol.declarations)) {
      return symbol.declarations.filter(it => (ts.isFunctionDeclaration(it) || ts.isInterfaceDeclaration(it) || ts.isVariableDeclaration(it) || ts.isClassDeclaration(it) || ts.isModuleDeclaration(it)));
    }

    return [];
  }
}
