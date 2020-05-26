import * as ts from "typescript";

export interface Dependency {
  fileName: string;

  merge(dependency: Dependency): Dependency;

  accept(node: ts.Node): boolean;
}

export class SymbolDependency {
  constructor(public node: ts.Node) {}
}

export class TranslateAllSymbolsDependency implements Dependency {
  constructor(public fileName: string) {
  }

  merge(dependency: Dependency): Dependency {
    return this;
  }

  accept(node: ts.Node): boolean {
    return true;
  }

  toString(): string {
    return `TranslateAllSymbolsDependency: ${this.fileName}`
  }
}

export class TranslateSubsetOfSymbolsDependency implements Dependency {
  private symbols: Set<ts.Node>;
  private parentUids: Set<ts.Node> = new Set<ts.Node>();

  constructor(public fileName: string, private symbolDependencies: Array<SymbolDependency>) {
    this.symbols = new Set(symbolDependencies.map(it => {
      let node = it.node;
      let parent = node.parent;
      while (parent) {
        if (ts.isModuleDeclaration(parent)) {
          this.parentUids.add(parent)
        }
        parent = parent.parent
      }
      return it.node;
    }));
  }

  merge(dependency: Dependency): Dependency {
    if (dependency instanceof TranslateAllSymbolsDependency) {
      return dependency;
    } else if (dependency instanceof TranslateSubsetOfSymbolsDependency) {
      return new TranslateSubsetOfSymbolsDependency(this.fileName, [...this.symbolDependencies].concat([...dependency.symbolDependencies]));
    } else {
      return this;
    }
  }

  accept(node: ts.Node): boolean {
    if (ts.isExportAssignment(node)) {
      return true;
    }

    if (this.symbols.has(node)) {
      return true;
    }

    if (ts.isModuleDeclaration(node) && this.parentUids.has(node)) {
        return true;
    }

    return false;
  }

  toString(): string {
    return `TranslateSubsetOfSymbolsDependency: ${this.fileName} [${this.symbols.size}]`;
  }
}