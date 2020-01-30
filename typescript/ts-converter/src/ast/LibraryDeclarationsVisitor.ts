import * as ts from "typescript";
import {Declaration} from "./ast";

export class LibraryDeclarationsVisitor {

  private processed = new Set<ts.Node>();
  private skipTypes = new Set([
    "Array",
    "Boolean",
    "Error",
    "Function",
    "Number",
    "RegExp",
    "ReadonlyArray",
    "String"
  ]);

  constructor(
    private declarations = new Map<string, Array<Declaration>>(),
    private typeChecker: ts.TypeChecker,
    private createDeclarations: (node: ts.Node) => Array<Declaration>,
    private libsSet: Set<string>
  ) {
  }

  private registerDeclaration(declaration) {
    if (this.processed.has(declaration)) {
      return;
    }

    this.processed.add(declaration);

    const sourceName = declaration.getSourceFile().fileName;

    if (!Array.isArray(this.declarations.get(sourceName))) {
      this.declarations.set(sourceName, []);
    }

    this.createDeclarations(declaration).forEach(declaration => {
      this.declarations.get(sourceName)!.push(declaration);
    });
  }

  private checkReferences(entity: ts.Node) {
    const symbol = this.typeChecker.getTypeAtLocation(entity).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      for (let declaration of symbol.declarations) {
        if (this.isLibDeclaration(declaration)) {
          this.registerDeclaration(declaration);
        }
      }
    }
  }

  process(node: ts.TypeNode) {
    const shouldNotBeProcessed = ts.isTypeReferenceNode(node) && this.skipTypes.has(node.typeName.getText());
    if (!shouldNotBeProcessed) {
      this.checkReferences(node)
    }
  }

  public forEachDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
    this.declarations.forEach(callback);
  }

  isLibDeclaration(source: ts.Node): boolean {
    return this.libsSet.has(source.getSourceFile().fileName);
  }

}