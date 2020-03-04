import * as ts from "typescript";
import {ExportContext} from "./ExportContext";

export interface Dependency {
  fileName: string;

  merge(dependency: Dependency): Dependency;

  accept(node: ts.Node): boolean;
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
}

export class TranslateSubsetOfSymbolsDependency implements Dependency {
  private symbols: Set<string>;

  constructor(public fileName: string, private exportContext: ExportContext, uids: Array<string>) {
    this.symbols = new Set(uids);
  }

  merge(dependency: Dependency): Dependency {
    if (dependency instanceof TranslateAllSymbolsDependency) {
      return dependency;
    } else if (dependency instanceof TranslateSubsetOfSymbolsDependency) {
      return new TranslateSubsetOfSymbolsDependency(this.fileName, this.exportContext, [...this.symbols].concat([...dependency.symbols]));
    } else {
      return this;
    }
  }

  accept(node: ts.Node): boolean {
    return this.symbols.has(this.exportContext.getUID(node)) || ts.isExportAssignment(node) || ts.isModuleDeclaration(node);
  }
}