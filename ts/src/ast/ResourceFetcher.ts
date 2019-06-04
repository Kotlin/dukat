

class ResourceFetcher {
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

    let curDir = ts.getDirectoryPath(fileName) +  "/";

    let referencedFiles = sourceFile.referencedFiles.map(
      referencedFile => ts.normalizePath(curDir + referencedFile.fileName)
    );

    let libReferences = sourceFile.libReferenceDirectives.map(libReference => {
      let libName = libReference.fileName.toLocaleLowerCase();
      return ts.libMap.get(libName);
    });

    referencedFiles.concat(libReferences).forEach(reference => this.build(reference));
    return this.resourceSet;
  }
}