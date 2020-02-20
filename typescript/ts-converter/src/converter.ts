import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript";
import {FileResolver} from "./FileResolver";
import {AstFactory} from "./ast/AstFactory";
import {SourceFileDeclaration} from "./ast/ast";
import * as declarations from "declarations";
import {SourceFileDeclarationProto} from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";
import {DeclarationsVisitor} from "./ast/DeclarationsVisitor";
import {ExportContext} from "./ExportContext";
import {DocumentCache} from "./DocumentCache";

function createFileResolver(): FileResolver {
  return new FileResolver();
}

let cache = new DocumentCache();

function getLibPaths(program: ts.Program, libPath: ts.SourceFile | undefined, libs: Set<string> = new Set()): Set<string> {
  if (libPath === undefined) {
    return libs;
  }

  let value = ts.normalizePath(libPath.fileName);
  if (libs.has(value)) {
    return libs;
  }

  libs.add(value);
  libPath.libReferenceDirectives.forEach(libReference => {
    getLibPaths(program, program.getLibFileFromReference(libReference), libs);
  });

  return libs;
}


class SourceBundleBuilder {
  private astFactory = new AstFactory();
  private program = this.createProgram();

  private libsSet = getLibPaths(this.program, this.program.getSourceFile(this.stdLib));

  private isLibSource(node: ts.Node): boolean {
    return this.libsSet.has(ts.normalizePath(node.getSourceFile().fileName));
  }

  private declarationsVisitor: DeclarationsVisitor;
  private astConverter: AstConverter;

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

    this.astConverter = new AstConverter(
      new ExportContext((node: ts.Node) => this.isLibSource(node)),
      this.program.getTypeChecker(),
      new DeclarationResolver(this.program, (declaration => {
        this.declarationsVisitor.visit(declaration);
      })),
      this.astFactory
    );
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

    let files = new Array<SourceFileDeclarationProto>();

    this.files.forEach(fileName => {
      files.push(...this.createSourceSet(fileName));
    });

    let sourceSetBundle = new declarations.SourceBundleDeclarationProto();

    this.declarationsVisitor.forEachDeclaration((nodes, resourceSource: ts.SourceCode) => {
      let resourceName = resourceSource.getSourceFile().fileName;

      let filterFunc = (node: ts.Node) => nodes.has(node) || ts.isExportAssignment(node);

      let modules: any[] = [];
      if (ts.isSourceFile(resourceSource)) {
        let packageName = this.isLibSource(resourceSource) ? "<LIBROOT>" : "<ROOT>";
        modules = [this.astConverter.createModuleFromSourceFile(resourceSource, this.astFactory.createIdentifierDeclarationAsNameEntity(packageName), filterFunc)]
      }

      files.push(...modules.map(moduleDeclaration => {
        return this.astFactory.createSourceFileDeclaration(
            resourceName, moduleDeclaration as any
        )
      }));
    });

    sourceSetBundle.setSourcesList([this.astFactory.createSourceSet(this.files, files)]);
    return sourceSetBundle;
  }
}

export function createBundle(stdlib: string, files: Array<string>) {
  return new SourceBundleBuilder(stdlib, files).createBundle();
}