import * as ts from "typescript-services-api";
import {TsInternals} from "../TsInternals";

export class ResourceFetcher {
  private resourceSet = new Set<string>();

  constructor(
    private sourceFileFetcher: (fileName: string) => ts.SourceFile | undefined
  ) {
  }

  private * references(fileName: string): IterableIterator<string> {
    if (this.resourceSet.has(fileName)) {
      return;
    }
    this.resourceSet.add(fileName);
    const sourceFile = this.sourceFileFetcher(fileName);

    if (!sourceFile) {
      return;
    }

    yield sourceFile.fileName;

    let tsInternals = ((ts as any) as TsInternals);
    let curDir = tsInternals.getDirectoryPath(fileName) + "/";

    for (let referencedFile of sourceFile.referencedFiles) {
      yield* this.references(tsInternals.normalizePath(curDir + referencedFile.fileName));
    }

    if (sourceFile.resolvedTypeReferenceDirectiveNames) {
      for (let [_, referenceDirective] of sourceFile.resolvedTypeReferenceDirectiveNames) {
        if (referenceDirective && (typeof referenceDirective.resolvedFileName == "string")) {
          yield* this.references(tsInternals.normalizePath(referenceDirective.resolvedFileName));
        }
      }
    }

    for (let importDeclaration of sourceFile.imports) {
      const module = ts.getResolvedModule(sourceFile, importDeclaration.text);
      if (module && (typeof module.resolvedFileName == "string")) {
        yield* this.references(tsInternals.normalizePath(module.resolvedFileName));
      }
    }

  }

  * resources(fileName: string): IterableIterator<ts.SourceFile> {
    for (let reference of this.references(fileName)) {
      yield this.getSourceFile(reference);
    }
  }

  getSourceFile(fileName: string): ts.SourceFile {
    return this.sourceFileFetcher(fileName);
  }
}