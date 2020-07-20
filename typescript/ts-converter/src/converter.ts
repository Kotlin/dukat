import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript";
import {AstFactory} from "./ast/AstFactory";
import {SourceFileDeclaration} from "./ast/ast";
import * as declarations from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";
import {ExportContext} from "./ExportContext";
import {DocumentCache} from "./DocumentCache";
import {DependencyBuilder} from "./DependencyBuilder";

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
  private program: ts.Program;
  private libsSet: Set<string>;

  private isLibSource(node: ts.Node | string): boolean {
    let src = (typeof node == "string") ? node : (node.getSourceFile().fileName);
    return this.libsSet.has(ts.normalizePath(src));
  }

  private astConverter: AstConverter;
  private dependencyBuilder: DependencyBuilder;

  constructor(
    private tsConfig: string | null,
    private stdLib: string,
    private emitDiagnostics: boolean,
    originalFiles: Array<string>
  ) {
    this.program = this.createProgram(originalFiles);

    if (emitDiagnostics) {
      this.printDiagnostics(this.program);
    }

    this.libsSet = getLibPaths(this.program, this.program.getSourceFile(this.stdLib));

    let dependencyBuilder = new DependencyBuilder(this.program);
    originalFiles.forEach(file => {
      let sourceFile = this.program.getSourceFile(file);
      if (sourceFile) {
        dependencyBuilder.buildDependencies(sourceFile);
      }
    });

    this.dependencyBuilder = dependencyBuilder;

    this.astConverter = new AstConverter(
      new ExportContext(),
      this.program.getTypeChecker(),
      new DeclarationResolver(this.program),
      (node: ts.Node) => this.isLibSource(node)
    );
  }

  private printDiagnostics(program: ts.Program) {
    let diagnostics = ts.getPreEmitDiagnostics(program).concat(program.emit().diagnostics);

    if (diagnostics.length > 0) {
      diagnostics.forEach(diagnostic => {
        let { line, character } = diagnostic.file.getLineAndCharacterOfPosition(diagnostic.start);
        let message = ts.flattenDiagnosticMessageText(diagnostic.messageText, "\n");
        console.log(`[warning] [tsc] ${message} ${diagnostic.file.fileName}:${line + 1}:${character + 1}`);
      });
    }
  }

  private createProgram(files: Array<string>): ts.Program {
    let host = new DukatLanguageServiceHost(this.tsConfig, this.stdLib);

    files.forEach(fileName => host.register(fileName));
    let languageService = ts.createLanguageService(host, (ts as any).createDocumentRegistryInternal(void 0, void 0, cache || void 0));

    const program = languageService.getProgram();

    if (program == null) {
      throw new Error("failed to create languageService");
    }

    return program;
  }

  createBundle(): declarations.SourceSetDeclarationProto {
    let sourceSet = new declarations.SourceSetDeclarationProto();

    let sourceFiles = new Array<SourceFileDeclaration>();

    this.dependencyBuilder.forEachDependency(dep => {
      sourceFiles.push(this.astConverter.createSourceFileDeclaration(this.program.getSourceFile(dep.fileName), node => dep.accept(node)));
    });

    sourceSet.setSourcesList(sourceFiles);
    return sourceSet;
  }
}

export function createSourceSet(tsConfig: string | null, stdlib: string, emitDiagnostics: boolean, files: Array<string>): declarations.SourceSetDeclarationProto  {
  return new SourceBundleBuilder(tsConfig, stdlib, emitDiagnostics, files).createBundle();
}