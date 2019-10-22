import * as ts from "typescript-services-api";

export class LibraryDeclarationsVisitor {
  private visited = new Set<ts.Node>();
  private libDeclarations = new Map<string, Array<ts.Node>>();

  constructor(
    private typeChecker: ts.TypeChecker
  ) {
  }

  private registerDeclaration(declaration) {
    let sourceName = declaration.getSourceFile().fileName;

    if (!Array.isArray(this.libDeclarations.get(sourceName))) {
      this.libDeclarations.set(sourceName, []);
    }

    let declarations = this.libDeclarations.get(sourceName)!;
    declarations.push(declaration);
  }

  private checkLibReferences(entity: ts.Node) {
    let symbol = this.typeChecker.getTypeAtLocation(entity).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      for (let declaration of symbol.declarations) {
        if (!this.visited.has(declaration)) {
          this.visited.add(declaration);
          if (ts.isClassDeclaration(declaration) || ts.isInterfaceDeclaration(declaration)) {
            this.registerDeclaration(declaration);
            this.visit(declaration);
          }
        }
      }
    }
  }

  visit(node: ts.Node) {
    node.forEachChild(declaration => {
      if (ts.isHeritageClause(declaration)) {
        for (let type of declaration.types) {
          this.checkLibReferences(type);
        }
      } else if (ts.isTypeNode(declaration)) {
        this.checkLibReferences(declaration);
      }
      this.visit(declaration);
    });
  }

  public forEachLibDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
    this.libDeclarations.forEach(callback);
  }

}