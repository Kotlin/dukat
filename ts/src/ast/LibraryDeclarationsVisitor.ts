import {AstConverter} from "../AstConverter";

import * as ts from "typescript-services-api";
import {createLogger} from "../Logger";
import {Declaration} from "./ast";

export class LibraryDeclarationsVisitor {

  private log = createLogger("LibraryDeclarationsVisitor");
  private libDeclarations = new Map<string, Array<Declaration>>();
  private visited = new Set<ts.Node>();

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
    if (symbol && Array.isArray(symbol.declarations)) {
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

              if (declaration.name) {
                if (declaration.heritageClauses) {
                  declaration.heritageClauses.forEach(heritageClause => {
                    this.visit(heritageClause, declaration);
                  });
                }
              }
            }
          } else if (ts.isInterfaceDeclaration(declaration)) {
            let interfaceDeclaration = this.astConverter.convertInterfaceDeclaration(declaration, false);
            if (!Array.isArray(this.libDeclarations.get(sourceName))) {
              this.libDeclarations.set(sourceName, []);
            }
            (this.libDeclarations.get(sourceName) as Array<Declaration>).push(interfaceDeclaration);

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
      }
      else {
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


}