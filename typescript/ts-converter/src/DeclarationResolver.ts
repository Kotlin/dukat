import * as ts from "typescript-services-api";

export class DeclarationResolver {
  constructor(private languageService: ts.LanguageService) {}

  resolve(node: ts.Node, fileName: string): ReadonlyArray<ts.DefinitionInfo> | undefined {
    let definitions = this.languageService.getDefinitionAtPosition(fileName, node.end);
    return definitions;
  }
}
