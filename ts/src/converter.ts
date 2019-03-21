/// <reference path="../node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../node_modules/typescript/lib/tsserverlibrary.d.ts"/>
/// <reference path="../node_modules/typescript/lib/typescript.d.ts"/>


if (typeof ts == "undefined") {
    (global as any).ts = require("typescript/lib/tsserverlibrary");
}

declare function print(...arg: any[]): void;

declare function println(arg: String): void;

interface FileResolver {
    resolve(fileName: string): string;
}


if (typeof console == "undefined") {
    (global as any).console = {
        log: (...args: any[]) => {
            if (typeof println == "function") {
                println(args.map(it => String(it)).join(", "));
            }
        }
    }
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


if (typeof module != "undefined") {
    module.exports = main;
}
