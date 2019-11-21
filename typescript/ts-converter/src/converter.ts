import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript-services-api";
import {createLogger} from "./Logger";
import {FileResolver} from "./FileResolver";
import {AstFactory} from "./ast/AstFactory";
import {Declaration, SourceBundle, SourceFileDeclaration, SourceSet} from "./ast/ast";
import * as declarations from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";
import {ResourceFetcher} from "./ast/ResourceFetcher";
import {LibraryDeclarationsVisitor} from "./ast/LibraryDeclarationsVisitor";
import {ExportContext} from "./ExportContext";

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

function createSourceSet(fileName: string, stdlib: string, packageNameString: string, libDeclarations: Map<string, Array<ts.Node>>): SourceSet {
    let host = new DukatLanguageServiceHost(createFileResolver(), stdlib);
    host.register(fileName);

    let logger = createLogger("converter");

    let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));

    const program = languageService.getProgram();

    if (program == null) {
        throw new Error(`failed to create languageService ${fileName}`)
    }

    let astFactory = createAstFactory();
    let packageName = astFactory.createIdentifierDeclarationAsNameEntity(packageNameString);
    let libChecker = (node: ts.Node) => program.isSourceFileDefaultLibrary(node.getSourceFile());
    let libVisitor = new LibraryDeclarationsVisitor(
      libDeclarations,
      program.getTypeChecker(),
      libChecker,
      (node: ts.Node) => astConverter.convertTopLevelStatement(node)
    );

    let astConverter: AstConverter = new class extends AstConverter {
        visitType(type: ts.TypeNode): void {
            libVisitor.process(type);
        }
    }(
      packageName,
      new ResourceFetcher(fileName, (fileName: string) => program.getSourceFile(fileName)),
      new ExportContext(libChecker),
      program.getTypeChecker(),
      new DeclarationResolver(program),
      astFactory
    );

    let sourceSet = astConverter.createSourceSet(fileName);
    astConverter.printDiagnostics();
    return sourceSet;
}

export function translate(stdlib: string, packageName: string, files: Array<string>): SourceBundle {
    let libDeclarations = new Map<string, Array<Declaration>>();
    let sourceSets = files.map(fileName => createSourceSet(fileName, stdlib, packageName, libDeclarations));

    let sourceSetBundle = new declarations.SourceBundleDeclarationProto();

    let astFactory = createAstFactory();
    let libRootUid = "<LIBROOT>";

    let libFiles: Array<SourceFileDeclaration> = [];
    libDeclarations.forEach((declarations, resourceName) => {
            libFiles.push(astFactory.createSourceFileDeclaration(
              resourceName, astFactory.createModuleDeclaration(
                astFactory.createIdentifierDeclarationAsNameEntity(libRootUid),
                declarations,
                [],
                [],
                libRootUid,
                libRootUid,
                true
              ), []
            ));
    });

    sourceSets.push(astFactory.createSourceSet(libRootUid, libFiles));

    sourceSetBundle.setSourcesList(sourceSets);
    return sourceSetBundle;
}


function createBundle(stdlib: string, packageName:string, files: Array<string>) {
    let sourceSetBundle = translate(stdlib, packageName, files);
    return sourceSetBundle;
}

(global as any).createBundle = createBundle;
