import * as ts from "typescript";

type DefinitionData = {fileName: string}

export class DeclarationResolver {

  constructor(private program: ts.Program) {
  }

  resolve(node: ts.Node): ReadonlyArray<DefinitionData> | undefined {
    let typeChecker = this.program.getTypeChecker();
    let symbol = typeChecker.getSymbolAtLocation(node);

    if (symbol) {
      let symbols: Array<DefinitionData> = [];
      for (let declaration of symbol.declarations) {
        if (ts.isFunctionDeclaration(declaration) || ts.isInterfaceDeclaration(declaration) || ts.isVariableDeclaration(declaration)) {
          symbols.push({fileName: declaration.getSourceFile().fileName})
        }
      }

      return symbols;
    }

    return undefined;
  }
}
