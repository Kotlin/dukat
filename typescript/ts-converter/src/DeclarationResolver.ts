import * as ts from "typescript";


export class DeclarationResolver {

  constructor(private program: ts.Program, private onResolve: (declaration: ts.Node) => void) {
  }

  resolve(node: ts.Node): ReadonlyArray<ts.Node> | undefined {
    let typeChecker = this.program.getTypeChecker();
    let symbol = typeChecker.getSymbolAtLocation(node);

    if (symbol) {
      return symbol.declarations.filter(it => (ts.isFunctionDeclaration(it) || ts.isInterfaceDeclaration(it) || ts.isVariableDeclaration(it)));
    }

    return undefined;
  }
}
