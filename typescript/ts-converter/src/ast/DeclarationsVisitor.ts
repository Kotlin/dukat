import * as ts from "typescript";

export type RootNode = ts.SourceFile | ts.ModuleBlock;

function getContainer(node: ts.Node): RootNode {
  let parent = node.parent;
  while (parent) {
    if (ts.isModuleBlock(parent)) {
      return parent;
    }

    parent = parent.parent;
  }

  return node.getSourceFile();
}

export class DeclarationsVisitor {

  private processed = new Set<ts.Node>();
  private declarations = new Map<RootNode, Array<ts.Node>>();

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
    private typeChecker: ts.TypeChecker,
    private libsSet: Set<string>,
    private files: Set<string>
  ) {
  }

  private registerDeclaration(declaration) {
    if (this.processed.has(declaration)) {
      return;
    }

    this.processed.add(declaration);

    this.visit(declaration);

    const sourceFile = declaration.getSourceFile();

    if (!Array.isArray(this.declarations.get(sourceFile))) {
      this.declarations.set(sourceFile, []);
    }

    this.declarations.get(sourceFile)!.push(declaration);
  }

  private checkReferences(node: ts.Node) {
    const symbol = this.typeChecker.getTypeAtLocation(node).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      for (let declaration of symbol.declarations) {
        if (this.isTransientDependency(declaration)) {
          this.registerDeclaration(declaration);
        }
      }
    }
  }

  visit(declaration: ts.Node) {
    ts.forEachChild(declaration, node => {
      this.check(node);
    });
  }

  check(node: ts.TypeNode) {
    const shouldNotBeProcessed = ts.isTypeReferenceNode(node) && this.skipTypes.has(node.typeName.getText());
    if (!shouldNotBeProcessed) {
      this.checkReferences(node)
    }
  }

  public forEachDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
    this.declarations.forEach(callback);
  }

  isTransientDependency(node: string | ts.Node): boolean {
    const fileName = (typeof node == "string") ? node : node.getSourceFile().fileName;
    return !this.files.has(fileName);
  }

  isLibDeclaration(source: string | ts.Node): boolean {
    const fileName = (typeof source == "string") ? source : source.getSourceFile().fileName;
    return this.libsSet.has(fileName);
  }

}