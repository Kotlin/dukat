/// <reference path="../build/package/node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../build/package/node_modules/typescript/lib/tsserverlibrary.d.ts"/>
/// <reference path="../build/package/node_modules/typescript/lib/typescript.d.ts"/>

declare function require(path: string): any;

let declarations = require("declarations");

interface FileResolver {
    resolve(fileName: string): string;
}

declare interface DocumentCache {
    setDocument(key: string, path: string, sourceFile: ts.SourceFile): void;
    getDocument(key: string, path: string): ts.SourceFile | undefined;
}

function main(fileName: string, packageName: NameEntity, cache?: DocumentCache) {
    let host = new DukatLanguageServiceHost(createFileResolver());
    host.register(fileName);

    let logger = createLogger("converter");

    logger.debug(`using document cache: ${!!cache}`);
    let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));

    const program = languageService.getProgram();

    if (program == null) {
        throw new Error(`failed to create languageService ${fileName}`)
    }

    const sourceFile = program.getSourceFile(fileName);

    if (sourceFile == null) {
        throw new Error(`failed to resolve ${fileName}`)
    } else {
        let astConverter: AstConverter = new AstConverter(
            fileName,
            packageName,
            program.getTypeChecker(),
            (fileName: string) => program.getSourceFile(fileName),
            (node: ts.Node, fileName: string) => languageService.getDefinitionAtPosition(fileName, node.end),
            createAstFactory()
        );

        return astConverter.createSourceSet();
    }
}

declare var global: any;
global.main = main;