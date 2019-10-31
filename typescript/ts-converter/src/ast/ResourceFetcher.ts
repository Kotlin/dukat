import * as ts from "typescript-services-api";
import {TsInternals} from "../TsInternals";

export class ResourceFetcher {
  private resourceSet = new Set<string>();

  constructor(
    private sourceFileFetcher: (fileName: string) => ts.SourceFile | undefined,
    fileName: string
  ) {
    this.build(fileName);
  }

  private build(fileName: string): Set<string> {
    if (this.resourceSet.has(fileName)) {
      return this.resourceSet;
    }
    this.resourceSet.add(fileName);
    const sourceFile = this.sourceFileFetcher(fileName);

    if (!sourceFile) {
      return this.resourceSet;
    }

    let tsInternals = ((ts as any) as TsInternals);
    let curDir = tsInternals.getDirectoryPath(fileName) + "/";

    let referencedFiles = sourceFile.referencedFiles.map(
      referencedFile => tsInternals.normalizePath(curDir + referencedFile.fileName)
    );

    let referencedTypeFiles: Array<string> = [];

    if (sourceFile.resolvedTypeReferenceDirectiveNames) {
      sourceFile.resolvedTypeReferenceDirectiveNames.forEach(
        referenceDirective => {
          if (referenceDirective && (typeof referenceDirective.resolvedFileName == "string")) {
            referencedTypeFiles.push(tsInternals.normalizePath(referenceDirective.resolvedFileName))
          }
        }
      );
    }

    let libReferences = sourceFile.libReferenceDirectives.map(libReference => {
      let libName = libReference.fileName.toLocaleLowerCase();
      return tsInternals.libMap.get(libName);
    });

    let importFiles: Array<string> = [];
    sourceFile.imports.forEach(importDeclaration => {
      var module = ts.getResolvedModule(sourceFile, importDeclaration.text);
      if (module && (typeof module.resolvedFileName == "string")) {
        importFiles.push(tsInternals.normalizePath(module.resolvedFileName))
      }
    });

    let references = referencedFiles
      .concat(referencedTypeFiles)
      .concat(libReferences)
      .concat(importFiles);

    references.forEach(reference => this.build(reference));
    return this.resourceSet;
  }

  forEachReference(handler: (resourceName: string) => void) {
    this.resourceSet.forEach(handler);
  }
}