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

  constructor(
    private stdLib: string
  ) {
  }

<<<<<<< HEAD
  private createSourceSet(program: ts.Program, fileName: string, packageNameString: string): Array<SourceFileDeclaration> {
=======
  private createSourceSet(program: ts.Program, libs: Set<string>, fileName: string, packageNameString: string): Array<SourceFileDeclaration> {
>>>>>>> @{-1}
    let logger = createLogger("converter");

    let packageName = this.astFactory.createIdentifierDeclarationAsNameEntity(packageNameString);
    let libChecker = (node: ts.Node) => libs.has(node.getSourceFile().fileName);
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
    let host = new DukatLanguageServiceHost(createFileResolver(), this.stdLib);
    files.forEach(fileName => host.register(fileName));
    let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));
    const program = languageService.getProgram();

    if (program == null) {
      throw new Error("failed to create languageService");
    }

<<<<<<< HEAD
    let sourceSets = files.map(fileName => {
      return this.astFactory.createSourceSet(fileName, this.createSourceSet(program, fileName, packageName));
=======
    const libs = getLibPaths(program, program.getSourceFile(this.stdLib), ts.getDirectoryPath(this.stdLib));
    let sourceSets = files.map(fileName => {
      return this.astFactory.createSourceSet(fileName, this.createSourceSet(program, libs, fileName, packageName));
>>>>>>> @{-1}
    });

    let sourceSetBundle = new declarations.SourceBundleDeclarationProto();

    let libRootUid = "<LIBROOT>";

    let libFiles: Array<SourceFileDeclaration> = [];
    this.libDeclarations.forEach((declarations, resourceName) => {
      libFiles.push(this.astFactory.createSourceFileDeclaration(
        resourceName, this.astFactory.createModuleDeclaration(
          this.astFactory.createIdentifierDeclarationAsNameEntity(libRootUid),
          [],
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

function getLibPaths(program: ts.Program, libPath: ts.SourceFile | undefined, defaultLibraryPath: string, libs: Set<string> = new Set()) {
  if (libPath === undefined) {
    return libs;
  }

  if (libs.has(libPath.fileName)) {
    return libs;
  }

  libs.add(libPath.fileName);
  libPath.libReferenceDirectives.forEach(libReference => {
    getLibPaths(program, program.getLibFileFromReference(libReference), defaultLibraryPath, libs);
  });

  return libs;
}

export function createBundle(stdlib: string, packageName: string, files: Array<string>) {
  return new SourceBundleBuilder(stdlib).createBundle(packageName, files);
}