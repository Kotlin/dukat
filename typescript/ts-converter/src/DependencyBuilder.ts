import * as ts from "typescript";
import {tsInternals} from "./TsInternals";

import {
  Dependency,
  TranslateAllSymbolsDependency,
  TranslateSubsetOfSymbolsDependency
} from "./Dependency";
import {resolveDeclarations} from "./ExportContext";

export class DependencyBuilder {
  private dependencies = new Map<string, Dependency>();
  private visitedFiles = new Set<string>();
  private typeChecker = this.program.getTypeChecker();
  private checkedReferences = new Set<ts.Node>();

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

    this.registerDependency(new TranslateAllSymbolsDependency(source.fileName));

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
    private program: ts.Program
  ) {
  }

  private checkReferences(node: ts.Node) {
    let declarations = resolveDeclarations(node, this.typeChecker);

    for (let declaration of declarations) {
      if (this.checkedReferences.has(declaration)) {
        return;
      }
      this.checkedReferences.add(declaration);
      let sourceFile = declaration.getSourceFile();

      this.registerDependency(TranslateSubsetOfSymbolsDependency.create(sourceFile.fileName, [
        declaration
      ]));

      sourceFile.forEachChild(node => {
        if (ts.isImportDeclaration(node)) {
          this.visit(node)
        }
      });

      declaration.forEachChild(node => this.visit(node));
    }
  }

  private resolveModulePath(node: ts.Expression): string | null {
    const module = ts.getResolvedModule(node.getSourceFile(), node.text);
    if (module && (typeof module.resolvedFileName == "string")) {
      return tsInternals.normalizePath(module.resolvedFileName);
    }
    return null;
  }


  private visit(node: ts.Node) {
    if (ts.isNamedImports(node)) {
      for (let element of node.elements) {
        this.checkReferences(element.name);
      }
    } else if (ts.isTypeReferenceNode(node)) {
      this.checkReferences(node.typeName);
    } else if (ts.isInterfaceDeclaration(node)) {
      this.checkReferences(node);
    } else if (ts.isTypeAliasDeclaration(node)) {
      this.checkReferences(node.type)
    } else if (ts.isHeritageClause(node)) {
      for (let type of node.types) {
        this.checkReferences(type);
      }
    } else if (ts.isExportDeclaration(node)) {
      if (node.exportClause) {
        if (Array.isArray(node.exportClause.elements)) {
          node.exportClause.elements.forEach(exportSpecifier => {
            this.checkReferences(exportSpecifier.name);
          });
        }
      } else {
        let resolvedModuleName = this.resolveModulePath(node.moduleSpecifier);
        if (resolvedModuleName) {
          this.registerDependency(new TranslateAllSymbolsDependency(resolvedModuleName));
        }
      }
    } else if (ts.isCallExpression(node)) {
      this.checkReferences(node.expression)
    }
    ts.forEachChild(node, node => this.visit(node));
  }

  forEachDependency(handler: (dep: Dependency) => void) {
    this.dependencies.forEach(dep => {
      handler(dep)
    })
  }
}