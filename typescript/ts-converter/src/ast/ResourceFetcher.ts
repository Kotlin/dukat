/// <reference path="../../build/package/node_modules/typescript/lib/typescriptServices.d.ts"/>

import * as ts from "typescript-services-api";

//Declarations that declared inside namespace marked as internal and not exist inside typescriptServices.d.ts and typescript.d.ts, but available at runtime
interface TsInternals {
    normalizePath(path: string): string;
    getDirectoryPath(path: string): string;
    libMap : { get(path: string) : string };
}


export class ResourceFetcher {
    private resourceSet = new Set<string>();

  constructor(
    private sourceFileFetcher: (fileName: string) => ts.SourceFile | undefined,
  ) {}

   build(fileName: string): Set<string> {
    this.resourceSet.add(fileName);
    const sourceFile = this.sourceFileFetcher(fileName);

    if (!sourceFile) {
      return this.resourceSet;
    }

    let curDir = ((ts as any) as TsInternals).getDirectoryPath(fileName) +  "/";

    let referencedFiles = sourceFile.referencedFiles.map(
      referencedFile => ((ts as any) as TsInternals).normalizePath(curDir + referencedFile.fileName)
    );

    let libReferences = sourceFile.libReferenceDirectives.map(libReference => {
      let libName = libReference.fileName.toLocaleLowerCase();
      return ((ts as any) as TsInternals).libMap.get(libName);
    });

    referencedFiles.concat(libReferences).forEach(reference => this.build(reference));
    return this.resourceSet;
  }
}