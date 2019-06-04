class LibraryDeclarationsVisitor {

  private log = createLogger("LibraryDeclarationsVisitor");
  private libDeclarations = new Map<string, Array<Declaration>>();

  constructor(
    private resources: Set<string>,
    private typeChecker: ts.TypeChecker,
    private astConverter: AstConverter
  ) {
  }

  public forEachLibDeclaration(callback: (value: Array<Declaration>, key: string) => void) {
    this.libDeclarations.forEach(callback);
  }

  private checkLibReferences(entity: ts.Node) {
    let symbol = this.typeChecker.getTypeAtLocation(entity).symbol;
    if (symbol) {
      for (let declaration of symbol.declarations) {
        let sourceFile = declaration.getSourceFile();

        let sourceName = sourceFile.fileName;
        if (sourceFile && !this.resources.has(sourceName)) {
          if (ts.isClassDeclaration(declaration)) {
            let classDeclaration = this.astConverter.convertClassDeclaration(declaration);
            if (classDeclaration) {
              if (!Array.isArray(this.libDeclarations.get(sourceName))) {
                this.libDeclarations.set(sourceName, []);
              }
              (this.libDeclarations.get(sourceName) as Array<Declaration>).push(classDeclaration);
            }
          } else if (ts.isInterfaceDeclaration(declaration)) {
            let interfaceDeclaration = this.astConverter.convertInterfaceDeclaration(declaration);
            if (!Array.isArray(this.libDeclarations.get(sourceName))) {
              this.libDeclarations.set(sourceName, []);
            }
            (this.libDeclarations.get(sourceName) as Array<Declaration>).push(interfaceDeclaration);
          }
        }

      }
    }
  }

  visit(node: ts.Node) {
    node.forEachChild(declaration => {
      if (ts.isHeritageClause(declaration)) {
        for (let type of declaration.types) {
          this.checkLibReferences(type);
        }
      } else {
        this.visit(declaration)
      }
    });
  }


}