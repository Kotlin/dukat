import * as ts from "typescript-services-api";

export class LibraryDeclarationsVisitor {
  private processed = new Set<ts.Node>();
  private libDeclarations = new Map<string, Array<ts.Node>>();
  private skipTypes = new Set([
    "Array",
    "Boolean",
    "Error",
    "Number",
    "ReadonlyArray",
    "String"
  ]);

  constructor(
    private typeChecker: ts.TypeChecker,
    private libChecker: (node: ts.Node) => boolean
  ) {
  }

  private registerDeclaration(declaration) {
    const sourceName = declaration.getSourceFile().fileName;

    if (!Array.isArray(this.libDeclarations.get(sourceName))) {
      this.libDeclarations.set(sourceName, []);
    }

    this.libDeclarations.get(sourceName)!.push(declaration);
  }

  private checkLibReferences(entity: ts.Node) {
    const symbol = this.typeChecker.getTypeAtLocation(entity).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      for (let declaration of symbol.declarations) {
        if (this.libChecker(declaration)) {
          if (!this.processed.has(declaration)) {
            this.processed.add(declaration);
            this.registerDeclaration(declaration);
          }
        }
      }
    }
  }

  process(node: ts.ExpressionWithTypeArguments | ts.TypeNode) {
    const shouldNotBeProcessed = ts.isTypeReferenceNode(node) && this.skipTypes.has(node.typeName.getText());
    if (!shouldNotBeProcessed) {
      this.checkLibReferences(node)
    }
  }

  public forEachLibDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
    this.libDeclarations.forEach(callback);
  }

}