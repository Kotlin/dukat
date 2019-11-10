import * as ts from "typescript-services-api";
import {TsInternals} from "../TsInternals";

export class ResourceFetcher {
  private resourceSet = new Set<string>();

  constructor(
    private fileName: string,
    private sourceFileFetcher: (fileName: string) => ts.SourceFile | undefined
  ) {
    this.build(fileName);
  }

  private build(fileName: string) {
    if (this.resourceSet.has(fileName)) {
      return;
    }
    this.resourceSet.add(fileName);
    const sourceFile = this.sourceFileFetcher(fileName);

    if (!sourceFile) {
      return;
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
      const module = ts.getResolvedModule(sourceFile, importDeclaration.text);
      if (module && (typeof module.resolvedFileName == "string")) {
        importFiles.push(tsInternals.normalizePath(module.resolvedFileName))
      }
    });

    let references = referencedFiles
      .concat(referencedTypeFiles)
      .concat(libReferences)
      .concat(importFiles);

    references.forEach(reference => this.build(reference));
  }

  forEachReference(handler: (resourceName: string) => void) {
    this.resourceSet.forEach(handler);
  }

  getSourceFile(fileName: string): ts.SourceFile {
    return this.sourceFileFetcher(fileName);
  }
}