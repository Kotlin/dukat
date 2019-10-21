import * as ts from "typescript-services-api";
import {ResourceFetcher} from "./ResourceFetcher";

export class LibraryDeclarationsVisitor {
  private visited = new Set<ts.Node>();
  private libDeclarations = new Map<string, Array<ts.Node>>();

  constructor(
    private resources: ResourceFetcher,
    private typeChecker: ts.TypeChecker
  ) {
  }

  private registerDeclaration(declaration) {
    let sourceFile = declaration.getSourceFile();
    let sourceName = sourceFile.fileName;

    if (!Array.isArray(this.libDeclarations.get(sourceName))) {
      this.libDeclarations.set(sourceName, []);
    }

    let declarations = this.libDeclarations.get(sourceName)!;
    declarations.push(declaration);
  }

  private checkLibReferences(entity: ts.Node) {
    let symbol = this.typeChecker.getTypeAtLocation(entity).symbol;
    if (symbol && Array.isArray(symbol.declarations)) {
      for (let declaration of symbol.declarations) {
        let sourceFile = declaration.getSourceFile();

        let sourceName = sourceFile.fileName;

        if (sourceFile && !this.resources.has(sourceName)) {
          if (ts.isClassDeclaration(declaration)) {
            this.registerDeclaration(declaration);
            if (declaration.name) {
              if (declaration.heritageClauses) {
                declaration.heritageClauses.forEach(heritageClause => {
                  this.visit(heritageClause, declaration);
                });
              }
            }
          } else if (ts.isInterfaceDeclaration(declaration)) {
            this.registerDeclaration(declaration);

            if (declaration.heritageClauses) {
              declaration.heritageClauses.forEach(heritageClause => {
                this.visit(heritageClause, declaration);
              });
            }
          }
        }

      }
    }
  }

  visit(node: ts.Node, classLikeOwner: ts.Node | null = null) {
    if (this.visited.has(node)) {
      return;
    }

    this.visited.add(node);

    node.forEachChild(declaration => {
      if (ts.isHeritageClause(declaration)) {
        for (let type of declaration.types) {
          this.checkLibReferences(type);
        }
      } else if (ts.isTypeNode(declaration)) {
        if (classLikeOwner) {
          this.checkLibReferences(declaration);
        }
      } else {
        if (ts.isInterfaceDeclaration(node) || ts.isClassDeclaration(node)) {
          if (node.heritageClauses) {
            this.visit(declaration, node);
          }
        } else {
          this.visit(declaration, classLikeOwner);
        }
      }
    });
  }

  public forEachLibDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
    this.libDeclarations.forEach(callback);
  }

}