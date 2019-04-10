/// <reference path="../build/package/node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../build/package/node_modules/typescript/lib/tsserverlibrary.d.ts"/>
/// <reference path="../build/package/node_modules/typescript/lib/typescript.d.ts"/>


interface FileResolver {
    resolve(fileName: string): string;
}

function main(fileName: string) {
    let host = new DukatLanguageServiceHost(createFileResolver());
    host.register(fileName);

    let languageService = ts.createLanguageService(host, ts.createDocumentRegistry());

    const program = languageService.getProgram();

    if (program == null) {
        throw new Error(`failed to create languageService ${fileName}`)
    }

    const sourceFile = program.getSourceFile(fileName);

    if (sourceFile == null) {
        throw new Error(`failed to resolve ${fileName}`)
    } else {
        let astConverter: AstConverter = new AstConverter(
            program.getTypeChecker(),
            (fileName: string) => program.getSourceFile(fileName),
            (node: ts.Node, fileName: string) => languageService.getDefinitionAtPosition(fileName, node.end),
            createAstFactory()
        );

        return astConverter.createSourceSet(fileName);
    }
}