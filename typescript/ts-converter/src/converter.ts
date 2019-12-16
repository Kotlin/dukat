import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript";
import {createLogger} from "./Logger";
import {FileResolver} from "./FileResolver";
import {AstFactory} from "./ast/AstFactory";
import {Declaration, SourceFileDeclaration} from "./ast/ast";
import * as declarations from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";
import {LibraryDeclarationsVisitor} from "./ast/LibraryDeclarationsVisitor";
import {ExportContext} from "./ExportContext";
import {AstVisitor} from "./AstVisitor";

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

class SourceBundleBuilder {
  private astFactory = new AstFactory();
  private libDeclarations = new Map<string, Array<Declaration>>();
  private fileDeclarationCache = new Map<string, SourceFileDeclaration>();

  constructor(
    private stdLib: string
  ) {
  }

  private createSourceSet(fileName: string, packageNameString: string): Array<SourceFileDeclaration> {
    let host = new DukatLanguageServiceHost(createFileResolver(), this.stdLib);
    host.register(fileName);

    let logger = createLogger("converter");

    let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));

    const program = languageService.getProgram();

    if (program == null) {
      throw new Error(`failed to create languageService ${fileName}`)
    }

    let packageName = this.astFactory.createIdentifierDeclarationAsNameEntity(packageNameString);
    let libChecker = (node: ts.Node) => program.isSourceFileDefaultLibrary(node.getSourceFile());
    let libVisitor = new LibraryDeclarationsVisitor(
      this.libDeclarations,
      program.getTypeChecker(),
      libChecker,
      (node: ts.Node) => astConverter.convertTopLevelStatement(node)
    );

    let astConverter: AstConverter = new AstConverter(
      packageName,
      new ExportContext(libChecker),
      program.getTypeChecker(),
      new DeclarationResolver(program),
      this.astFactory,
      new class implements AstVisitor {
        visitType(type: ts.TypeNode): void {
          libVisitor.process(type);
        }
      }
    );

    return this.createFileDeclarations(fileName, astConverter, program);
  }

  createFileDeclarations(fileName: string, astConverter: AstConverter, program: ts.Program, result: Map<string, SourceFileDeclaration> = new Map()): Array<SourceFileDeclaration> {
    if (result.has(fileName)) {
      return [];
    }
    let fileDeclaration = astConverter.createSourceFileDeclaration(program.getSourceFile(fileName));
    result.set(fileName, fileDeclaration);
    fileDeclaration
      .getReferencedfilesList()
      .forEach(resourceFileName => {
        this.createFileDeclarations(resourceFileName, astConverter, program, result);
      });

    return Array.from(result.values());
  }

  createBundle(packageName: string, files: Array<string>): declarations.SourceBundleDeclarationProto {
    let sourceSets = files.map(fileName => {
      return this.astFactory.createSourceSet(fileName, this.createSourceSet(fileName, packageName));
    });

    let sourceSetBundle = new declarations.SourceBundleDeclarationProto();

    let libRootUid = "<LIBROOT>";

    let libFiles: Array<SourceFileDeclaration> = [];
    this.libDeclarations.forEach((declarations, resourceName) => {
      libFiles.push(this.astFactory.createSourceFileDeclaration(
        resourceName, this.astFactory.createModuleDeclaration(
          this.astFactory.createIdentifierDeclarationAsNameEntity(libRootUid),
          declarations,
          [],
          [],
          libRootUid,
          libRootUid,
          true
        ), []
      ));
    });

    sourceSets.push(this.astFactory.createSourceSet(libRootUid, libFiles));

    sourceSetBundle.setSourcesList(sourceSets);
    return sourceSetBundle;
  }
}

export function createBundle(stdlib: string, packageName: string, files: Array<string>) {
  return new SourceBundleBuilder(stdlib).createBundle(packageName, files);
}