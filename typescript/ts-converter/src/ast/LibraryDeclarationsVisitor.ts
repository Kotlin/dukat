import * as ts from "typescript-services-api";
import {tsInternals} from "../TsInternals";

export class LibraryDeclarationsVisitor {
  private visited = new Set<ts.Node>();
  private libDeclarations = new Map<string, Array<ts.Node>>();
  private skipTypes = new Set([
    "Array",
    "Boolean",
    "Error",
    "Number",
    "ReadonlyArray",
    "String"
  ]);

  constructor(
    private typeChecker: ts.TypeChecker
  ) {
  }

  private isLibraryReference(declaration: ts.Node): boolean {
    //TODO: consider using ts.preProcessFile to acces PreProcessedFileInfo::isLibFile
    let resourceName = declaration.getSourceFile().fileName;
    let fileName = resourceName.replace(tsInternals.getDirectoryPath(resourceName), "");
    return  /lib(.*)\.d\.ts$/i.test(fileName);
  }

  private registerDeclaration(declaration) {
    let sourceName = declaration.getSourceFile().fileName;

    if (!Array.isArray(this.libDeclarations.get(sourceName))) {
      this.libDeclarations.set(sourceName, []);
    }

    this.libDeclarations.get(sourceName)!.push(declaration);
  }

  private checkLibReferences(entity: ts.Node) {
    let symbol = this.typeChecker.getTypeAtLocation(entity).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      for (let declaration of symbol.declarations) {
        if (this.isLibraryReference(declaration)) {
          if (!this.visited.has(declaration)) {
            this.visited.add(declaration);

            if (ts.isClassDeclaration(declaration) || ts.isInterfaceDeclaration(declaration)) {
              this.registerDeclaration(declaration);
              this.visit(declaration);
            }
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
      } else if (ts.isTypeNode(declaration)) {
        let shouldNotBeProcessed = ts.isTypeReferenceNode(declaration) && this.skipTypes.has(declaration.typeName.getText());
        if (!shouldNotBeProcessed) {
          this.checkLibReferences(declaration);
        }
      }
      this.visit(declaration);
    });
  }

  public forEachLibDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
    this.libDeclarations.forEach(callback);
  }

}