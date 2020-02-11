import * as ts from "typescript";

export type RootNode = ts.SourceFile | ts.ModuleBlock;

function getRootNode(node: ts.Node): RootNode {
  let parent = node.parent;
  while (parent) {
    if (ts.isModuleBlock(parent)) {
      return parent;
    }

    parent = parent.parent;
  }

  return node.getSourceFile();
}

function isTopLevel(node: ts.Node) {
  let res = false;

  if (ts.isClassDeclaration(node)) {
    res = true;
  }

  if (ts.isClassExpression(node)) {
    res = true;
  }

  if (ts.isEnumDeclaration(node)) {
    res = true;
  }

  if (ts.isInterfaceDeclaration(node)) {
    res = true;
  }

  if (ts.isModuleDeclaration(node)) {
    res = true;
  }

  if (ts.isSourceFile(node)) {
    res = true;
  }

  if (ts.isTypeAliasDeclaration(node)) {
    res = true;
  }

  if (ts.isJSDocTypedefTag(node)) {
    res = true;
  }

  if (ts.isJSDocCallbackTag(node)) {
    res = true;
  }

  if (ts.isArrowFunction(node) || ts.isFunctionDeclaration(node) || ts.isFunctionExpression(node)) {
    res = isTopLevelFunction(node);
  }

  return res;
}

function isTopLevelFunction(node: ts.ArrowFunction | ts.FunctionDeclaration | ts.FunctionExpression): boolean {
  let parent = node.parent;
  if (ts.isSourceFile(parent) || ts.isModuleDeclaration(parent)) {
    return true;
  }

  return false;
}

export abstract class DeclarationsVisitor {

  private processed = new Set<ts.Node>();
  private declarations = new Map<RootNode, Set<ts.Node>>();

  private skipTypes = new Set([
    "Array",
    "Date",
    "Boolean",
    "Error",
    "Event",
    "Function",
    "Number",
    "RegExp",
    "ReadonlyArray",
    "String"
  ]);

  constructor(
    private typeChecker: ts.TypeChecker
  ) {
  }

  private registerDeclaration(declaration) {
    let text = declaration.getText().substring(0, 50);
    if (this.processed.has(declaration)) {
      return;
    }

    if (isTopLevel(declaration)) {
      const rootNode = declaration.getSourceFile();

      if (!this.declarations.has(rootNode)) {
        this.declarations.set(rootNode, new Set());
      }

      this.declarations.get(rootNode)!.add(declaration);
    }

    this.processed.add(declaration);
    this.visit(declaration);
  }

  private checkReferences(node: ts.Node) {
    if (this.isLibDeclaration(node)) {
      if (this.skipTypes.has(node.name)) {
        return;
      }
    }

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
    if (ts.isTypeReferenceNode(declaration)) {
      if (!this.skipTypes.has(declaration.typeName.getText())) {
        this.checkReferences(declaration);
      }
    } else if (ts.isInterfaceDeclaration(declaration)) {
      this.checkReferences(declaration);
    } else if (ts.isTypeAliasDeclaration(declaration)) {
      this.checkReferences(declaration.type)
    } else if (ts.isHeritageClause(declaration)) {
      for (let type of declaration.types) {
        this.checkReferences(type);
      }
    }
    ts.forEachChild(declaration, node => this.visit(node));
  }

  public forEachDeclaration(callback: (value: Set<ts.Node>, key: string) => void) {
    this.declarations.forEach(callback);
  }

  abstract isTransientDependency(node: ts.Node): boolean;
  abstract isLibDeclaration(source: ts.Node): boolean;

}