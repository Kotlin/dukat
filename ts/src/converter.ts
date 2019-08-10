/// <reference path="../build/package/node_modules/typescript/lib/typescriptServices.d.ts"/>

declare function require(path: string): any;

interface FileResolver {
    resolve(fileName: string): string;
}

class DocumentCache {
    private myDocumentMap: Map<string, any> = new Map();

    setDocument(key: string, path: string, sourceFile: any) {
        this.myDocumentMap.set(path, sourceFile);
    }

    getDocument(key: string, path: string): any | undefined {
        return this.myDocumentMap.get(path);
    }
}

let cache = new DocumentCache();

function main(fileName: string, packageName: NameEntity) {
    let host = new DukatLanguageServiceHost(createFileResolver());
    host.register(fileName);

    let logger = createLogger("converter");

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