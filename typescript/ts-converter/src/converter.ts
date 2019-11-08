import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript-services-api";
import {createLogger} from "./Logger";
import {FileResolver} from "./FileResolver";
import {AstFactory} from "./ast/AstFactory";
import {SourceBundle, SourceSet} from "./ast/ast";
import * as declarations from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";

function createAstFactory(): AstFactory {
    return new AstFactory();
}

function createFileResolver(): FileResolver {
    return new FileResolver();
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

function createSourceSet(fileName: string, stdlib: string, packageNameString: string): SourceSet {
    let host = new DukatLanguageServiceHost(createFileResolver(), stdlib);
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
        let astFactory = createAstFactory();
        let packageName = astFactory.createIdentifierDeclarationAsNameEntity(packageNameString);
        let astConverter: AstConverter = new AstConverter(
          fileName,
          packageName,
          program.getTypeChecker(),
          (node: ts.Node) => program.isSourceFileDefaultLibrary(node.getSourceFile()),
          (fileName: string) => program.getSourceFile(fileName),
          new DeclarationResolver(program),
          astFactory
        );

        let sourceSet = astConverter.createSourceSet(fileName);
        astConverter.printDiagnostics();
        return sourceSet;
    }
}

export function translate(stdlib: string, packageName: string, files: Array<string>): SourceBundle {
    let sourceSets = files.map(fileName => createSourceSet(fileName, stdlib, packageName));
    let sourceSetBundle = new declarations.SourceSetBundleProto();
    sourceSetBundle.setSourcesList(sourceSets);
    return sourceSetBundle;
}


function createBundle(lib: string, packageName:string, files: Array<string>) {
    let sourceSetBundle = translate(lib, packageName, files);
    return sourceSetBundle;
}

(global as any).createBundle = createBundle;
