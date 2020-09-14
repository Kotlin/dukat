import * as ts from "typescript";

export interface Dependency {
  fileName: string;

  merge(dependency: Dependency): Dependency;

  accept(node: ts.Node): boolean;
}

function union<T>(...sets: Array<Set<T>>): Set<T> {
  let s = new Set<T>();
  for (let set of sets) {
    for (let item of set) {
      s.add(item)
    }
  }
  return s;
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
  private constructor(public fileName: string, private symbols: Set<ts.Node>, private parentUids: Set<ts.Node>) {}

  static create(fileName: string, symbolDependencies: Array<ts.Node>): TranslateSubsetOfSymbolsDependency {
    let symbols = new Set(symbolDependencies);
    let parentUids = new Set<ts.Node>();

    symbols.forEach(node => {
      let parent = node.parent;
      while (parent) {
        if (ts.isModuleDeclaration(parent) || ts.isVariableStatement(parent)) {
          parentUids.add(parent);
        }
        parent = parent.parent;
      }
    });

    return new TranslateSubsetOfSymbolsDependency(fileName, symbols, parentUids);
  }

  merge(dependency: Dependency): Dependency {
    if (dependency instanceof TranslateAllSymbolsDependency) {
      return dependency;
    } else if (dependency instanceof TranslateSubsetOfSymbolsDependency) {
      return new TranslateSubsetOfSymbolsDependency(
        this.fileName,
        union(this.symbols, dependency.symbols),
        union(this.parentUids, dependency.parentUids)
      );
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

    if ((ts.isModuleDeclaration(node) || ts.isVariableStatement(node)) && this.parentUids.has(node)) {
      return true;
    }

    return false;
  }

  toString(): string {
    return `TranslateSubsetOfSymbolsDependency: ${this.fileName} [${this.symbols.size}]`;
  }
}