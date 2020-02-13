import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript";
import {FileResolver} from "./FileResolver";
import {AstFactory} from "./ast/AstFactory";
import {SourceFileDeclaration} from "./ast/ast";
import * as declarations from "declarations";
import {SourceFileDeclarationProto} from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";
import {DeclarationsVisitor, RootNode} from "./ast/DeclarationsVisitor";
import {ExportContext} from "./ExportContext";
import {DocumentCache} from "./DocumentCache";

function createFileResolver(): FileResolver {
  return new FileResolver();
}

let cache = new DocumentCache();

function buildLibSet(stdLib: string): Set<string> {
  let host = new DukatLanguageServiceHost(new FileResolver(), stdLib);
  host.register(stdLib);
  let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));
  const program = languageService.getProgram();

  return getLibPaths(program, program.getSourceFile(stdLib), ts.getDirectoryPath(stdLib));
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


class SourceBundleBuilder {
  private astFactory = new AstFactory();
  private program = this.createProgram();

  private libsSet = getLibPaths(this.program, this.program.getSourceFile(this.stdLib), ts.getDirectoryPath(this.stdLib));

  private isLibSource(node: ts.Node): boolean {
    return this.libsSet.has(node.getSourceFile().fileName)
  }

  private declarationsVisitor: DeclarationsVisitor;
  private astConverter: AstConverter = this.createAstConverter();

  constructor(
      private stdLib: string,
      private files: Array<string>
  ) {

    let filesSet = new Set(this.files.map(file => ts.normalizePath(file)));
    let outerThis = this;

    this.declarationsVisitor = new class extends DeclarationsVisitor {
      isLibDeclaration(source: ts.Node): boolean {
        return outerThis.isLibSource(source);
      }

      isTransientDependency(node: ts.Node): boolean {
        return !filesSet.has(node.getSourceFile().fileName);
      }
    }(
        this.program.getTypeChecker()
    );

  }

  private createAstConverter(): AstConverter {
    let astConverter = new AstConverter(
        new ExportContext((node: ts.Node) => this.isLibSource(node)),
        this.program.getTypeChecker(),
        new DeclarationResolver(this.program),
        this.astFactory
    );

    return astConverter;
  }

  private createSourceSet(fileName: string): Array<SourceFileDeclaration> {
    return this.createFileDeclarations(fileName, this.program);
  }

  createFileDeclarations(fileName: string, program: ts.Program): Array<SourceFileDeclaration> {
    let sourceFile = program.getSourceFile(fileName);
    this.declarationsVisitor.visit(sourceFile);
    return [this.astConverter.createSourceFileDeclaration(sourceFile)];
  }

  private createProgram(): ts.Program {
    let host = new DukatLanguageServiceHost(createFileResolver(), this.stdLib);
    this.files.forEach(fileName => host.register(fileName));
    let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));
    const program = languageService.getProgram();

    if (program == null) {
      throw new Error("failed to create languageService");
    }

    return program;
  }

  createBundle(): declarations.SourceBundleDeclarationProto {
    let sourceSets = this.files.map(fileName => {
      return this.astFactory.createSourceSet([fileName], this.createSourceSet(fileName));
    });

    let sourceSetBundle = new declarations.SourceBundleDeclarationProto();

    let files = new Array<SourceFileDeclarationProto>();
    this.declarationsVisitor.forEachDeclaration((nodes, resourceSource: RootNode) => {
      let resourceName = resourceSource.getSourceFile().fileName;

      console.log(`NODE => ${resourceSource.getSourceFile().fileName}`);
      let filterFunc = (node: ts.Node) => nodes.has(node);

      let modules: any[] = [];
      if (ts.isSourceFile(resourceSource)) {
        let packageName = this.isLibSource(resourceSource) ? "<LIBROOT>" : "<ROOT>";
        modules = [this.astConverter.createModuleFromSourceFile(resourceSource, this.astFactory.createIdentifierDeclarationAsNameEntity(packageName), filterFunc)]
      } else if (ts.isModuleDeclaration(resourceSource)) {
        modules = this.astConverter.convertModule(resourceSource, filterFunc)
      }

      for (let module of modules) {
        console.log(`MODULE ${module.getPackagename()}`);
      }

      files.push(...modules.map(moduleDeclaration => {
        return this.astFactory.createSourceFileDeclaration(
            resourceName, moduleDeclaration as any
        )
      }));
    });

    console.log(`FILES ${files.length}`);
    sourceSets.push(this.astFactory.createSourceSet(["<TRANSIENT>"], files));

    sourceSetBundle.setSourcesList(sourceSets);
    return sourceSetBundle;
  }
}

export function createBundle(stdlib: string, files: Array<string>) {
  return new SourceBundleBuilder(stdlib, files).createBundle();
}