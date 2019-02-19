/// <reference path="../node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../node_modules/typescript/lib/tsserverlibrary.d.ts"/>


if (typeof ts == "undefined") {
    (global as any).ts = require("typescript/lib/tsserverlibrary");
}

declare function print(...arg: any[]): void;

declare function uid(): string;

declare function println(arg: String): void;

declare class FileResolverV8 implements FileResolver {
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


interface FileResolver {
    resolve(fileName: string): string;
}

function main(nativeAstFactory: AstFactory, fileResolver: FileResolver, fileName: string) {
    if (fileResolver == null) {
        fileResolver = new FileResolverV8();
    }

    let documentRegistry = ts.createDocumentRegistry();

    let host = new DukatLanguageServiceHost(fileResolver);
    host.register(fileName);

    let languageService = ts.createLanguageService(host, documentRegistry);

    var program = languageService.getProgram();

    if (program == null) {
        throw new Error(`failed to create languageService ${fileName}`)
    }

    var sourceFile = program.getSourceFile(fileName);

    if (sourceFile == null) {
        throw new Error(`failed to resolve ${fileName}`)
    }

    // TODO: don't remeber how it's done in ts2kt, need to refresh my memories
    let packageNameFragments = sourceFile.fileName.split("/");
    let resourceName = packageNameFragments[packageNameFragments.length - 1].replace(".d.ts", "");
    let astConverter: AstConverter = nativeAstFactory == null ?
        new AstConverter(new AstFactoryV8(), program.getTypeChecker(), resourceName) : new AstConverter(nativeAstFactory, program.getTypeChecker(), resourceName);


    var declarations: Declaration[] = astConverter.convertDeclarations(sourceFile.statements as any);


    return astConverter.createDocumentRoot("__ROOT__", declarations, astConverter.convertModifiers(sourceFile.modifiers), uid())
}


if (typeof module != "undefined") {
    module.exports = main;
}
