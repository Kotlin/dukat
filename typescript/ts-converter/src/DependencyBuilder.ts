import * as ts from "typescript";
import {tsInternals} from "./TsInternals";
import {ExportContext} from "./ExportContext";
import {
  Dependency,
  SymbolDependency,
  TranslateAllSymbolsDependency,
  TranslateSubsetOfSymbolsDependency
} from "./Dependency";

export class DependencyBuilder {
  private dependencies = new Map<string, Dependency>();
  private visitedFiles = new Set<string>();
  private typeChecker = this.program.getTypeChecker();
  private checkedReferences = new Set<string>();

  private registerDependency(dependency: Dependency) {
    let currentDependency = this.dependencies.get(dependency.fileName);
    if (currentDependency) {
      this.dependencies.set(dependency.fileName, currentDependency.merge(dependency));
    } else {
      this.dependencies.set(dependency.fileName, dependency);
    }
  }

  buildDependencies(source: ts.SourceFile) {
    if (this.visitedFiles.has(source.fileName)) {
      return;
    }

    let curDir = ts.getDirectoryPath(source.fileName);

    this.registerDependency(new TranslateAllSymbolsDependency(ts.getNormalizedAbsolutePath(source.fileName, curDir)));

    source.referencedFiles.forEach(referencedFile => {
      let normalizedPath = ts.getNormalizedAbsolutePath(referencedFile.fileName, curDir);
      this.buildDependencies(this.program.getSourceFile(normalizedPath))
    });

    if (source.resolvedTypeReferenceDirectiveNames instanceof Map) {
      for (let [_, referenceDirective] of source.resolvedTypeReferenceDirectiveNames) {
        if (referenceDirective && referenceDirective.hasOwnProperty("resolvedFileName")) {
            this.buildDependencies(this.program.getSourceFile(tsInternals.normalizePath(referenceDirective.resolvedFileName)));
        }
      }
    }

    this.visit(source);
    this.visitedFiles.add(source.fileName);
  }

  constructor(
    private program: ts.Program,
    private exportContext: ExportContext
  ) {
  }

  private getDeclarations(node: ts.Node): Array<ts.Node> {
    let symbolAtLocation = this.typeChecker.getSymbolAtLocation(node);
    if (symbolAtLocation) {

      if (symbolAtLocation.flags & ts.SymbolFlags.Alias) {
        let aliasedSymbol = this.typeChecker.getAliasedSymbol(symbolAtLocation);
        if (aliasedSymbol && Array.isArray(aliasedSymbol.declarations)) {
          return aliasedSymbol.declarations;
        } else {
          return [];
        }
      }

      if (Array.isArray(symbolAtLocation.declarations)) {
        return symbolAtLocation.declarations;
      } else {
        let declaredTyped = this.typeChecker.getDeclaredTypeOfSymbol(symbolAtLocation);
        if (declaredTyped) {
          let resolvedASymbol = declaredTyped.symbol || declaredTyped.aliasSymbol;
          if (resolvedASymbol && Array.isArray(resolvedASymbol.declarations)) {
            return resolvedASymbol.declarations;
          }
        }
      }
    }

    let symbol = this.typeChecker.getTypeAtLocation(node).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      return symbol.declarations;
    }

    return [];
  }

  private createSymbolDependency(declaration: ts.Declaration): SymbolDependency | undefined {
    let uid = this.exportContext.getUID(declaration);
    if (this.checkedReferences.has(uid)) {
      return undefined;
    }
    this.checkedReferences.add(uid);

    let parent = declaration.parent;
    let parentModuleUids = new Array<string>();
    while (parent) {
      if (ts.isModuleDeclaration(parent)) {
        parentModuleUids.push(this.exportContext.getUID(parent));
      }
      parent = parent.parent
    }

    return new SymbolDependency(uid, parentModuleUids);
  }


  private checkReferences(node: ts.Node) {
    let declarations = this.getDeclarations(node);

    for (let declaration of declarations) {
      let symbolDependency = this.createSymbolDependency(declaration);

      if (symbolDependency) {
        let translateSubsetOfSymbolsDependency = new TranslateSubsetOfSymbolsDependency(declaration.getSourceFile().fileName, this.exportContext, [
          symbolDependency
        ]);
        this.registerDependency(translateSubsetOfSymbolsDependency);
        declaration.forEachChild(node => this.visit(node));
      }
    }
  }

  private visit(node: ts.Node) {
    if (ts.isTypeReferenceNode(node)) {
      this.checkReferences(node.typeName);
    } else if (ts.isInterfaceDeclaration(node)) {
      this.checkReferences(node);
    } else if (ts.isTypeAliasDeclaration(node)) {
      this.checkReferences(node.type)
    } else if (ts.isHeritageClause(node)) {
      for (let type of node.types) {
        this.checkReferences(type);
      }
    }
    ts.forEachChild(node, node => this.visit(node));
  }

  forEachDependency(handler: (dep: Dependency) => void) {
    this.dependencies.forEach(dep => {
      handler(dep)
    })
  }
}