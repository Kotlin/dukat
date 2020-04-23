import * as ts from "typescript";
import {ExportContext} from "./ExportContext";

export interface Dependency {
  fileName: string;

  merge(dependency: Dependency): Dependency;

  accept(node: ts.Node): boolean;
}

export class SymbolDependency {
  constructor(public uid: string, public parentModuleUids: Array<string>) {}
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
  private symbols: Set<string>;
  private parentUids: Set<string>;

  constructor(public fileName: string, private exportContext: ExportContext, private uids: Array<SymbolDependency>) {
    this.symbols = new Set(uids.map(it => it.uid));
    this.parentUids = new Set(uids.map(it => it.parentModuleUids).reduce((a, b) => a.concat(b)))
  }

  merge(dependency: Dependency): Dependency {
    if (dependency instanceof TranslateAllSymbolsDependency) {
      return dependency;
    } else if (dependency instanceof TranslateSubsetOfSymbolsDependency) {
      return new TranslateSubsetOfSymbolsDependency(this.fileName, this.exportContext, [...this.uids].concat([...dependency.uids]));
    } else {
      return this;
    }
  }

  accept(node: ts.Node): boolean {

    console.log(ts.SyntaxKind[node.kind]);

    if (ts.isExportAssignment(node)) {
      return true;
    }

    let uid = this.exportContext.getUID(node);

    console.log(uid);
    console.log(this.symbols);
    if (this.symbols.has(uid)) {
      return true;
    }

    if ((ts.isModuleDeclaration(node)) && (this.parentUids.has(uid))) {
      return true;
    }

    return false;
  }

  toString(): string {
    return `TranslateSubsetOfSymbolsDependency: ${this.fileName} [${this.symbols.size}]`;
  }
}