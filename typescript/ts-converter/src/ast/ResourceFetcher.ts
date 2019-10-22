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

    let libReferences = sourceFile.libReferenceDirectives.map(libReference => {
      let libName = libReference.fileName.toLocaleLowerCase();
      return tsInternals.libMap.get(libName);
    });

    referencedFiles.concat(libReferences).forEach(reference => this.build(reference));
    return this.resourceSet;
  }

  forEachReference(handler: (resourceName: string) => void) {
    this.resourceSet.forEach(handler);
  }
}