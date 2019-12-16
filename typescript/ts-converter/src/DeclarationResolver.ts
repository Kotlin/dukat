import * as ts from "typescript";

const MERGE_RESOLUTION_SYMBOLS = new Map([
  [ts.SyntaxKind.InterfaceDeclaration, new Set([
    ts.SyntaxKind.FunctionDeclaration,
    ts.SyntaxKind.InterfaceDeclaration,
    ts.SyntaxKind.VariableDeclaration
  ])],
  [ts.SyntaxKind.ModuleDeclaration, new Set([ts.SyntaxKind.ModuleDeclaration])]
]);

type DefinitionData = {fileName: string}

export class DeclarationResolver {

  constructor(private program: ts.Program) {
  }

  resolve(kind: ts.SyntaxKind, node: ts.Node): ReadonlyArray<DefinitionData> | undefined {
    let typeChecker = this.program.getTypeChecker();
    let symbol = typeChecker.getSymbolAtLocation(node);

    if (symbol) {
      let symbols: Array<DefinitionData> = [];
      for (let declaration of symbol.declarations) {
        let mergeAllowedSet = MERGE_RESOLUTION_SYMBOLS.get(kind);
        if (mergeAllowedSet && mergeAllowedSet.has(declaration.kind)) {
          symbols.push({fileName: declaration.getSourceFile().fileName})
        }
      }

      return symbols;
    }

    return undefined;
  }
}
