import {DukatLanguageServiceHost} from "./DukatLanguageServiceHost";
import {AstConverter} from "./AstConverter";
import * as ts from "typescript";
import {FileResolver} from "./FileResolver";
import {AstFactory} from "./ast/AstFactory";
import {Declaration, ModuleDeclaration, SourceFileDeclaration} from "./ast/ast";
import * as declarations from "declarations";
import {DeclarationResolver} from "./DeclarationResolver";
import {DeclarationsVisitor, RootNode} from "./ast/DeclarationsVisitor";
import {ExportContext} from "./ExportContext";
import {AstVisitor} from "./AstVisitor";
import {DocumentCache} from "./DocumentCache";
import * as fs from "fs";

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

  private declarationsVisitor = new DeclarationsVisitor(
      this.program.getTypeChecker(),
      getLibPaths(this.program, this.program.getSourceFile(this.stdLib), ts.getDirectoryPath(this.stdLib)),
      new Set(this.files.map(file => ts.normalizePath(file)))
  );
  private astConverter: AstConverter = this.createAstConverter(this.declarationsVisitor);

  constructor(
      private stdLib: string,
      private files: Array<string>
  ) {
  }

  private createAstConverter(declarationsVisitor: DeclarationsVisitor): AstConverter {
    let astConverter = new AstConverter(
        new ExportContext((node: ts.Node) => declarationsVisitor.isLibDeclaration(node)),
        this.program.getTypeChecker(),
        new DeclarationResolver(this.program),
        this.astFactory,
        new class implements AstVisitor {
          visitType(type: ts.TypeNode): void {
            declarationsVisitor.check(type);
          }
        }
    );

    return astConverter;
  }

  private createSourceSet(fileName: string): Array<SourceFileDeclaration> {
    return this.createFileDeclarations(fileName, this.program);
  }

  createFileDeclarations(fileName: string, program: ts.Program): Array<SourceFileDeclaration> {
    return [this.astConverter.createSourceFileDeclaration(program.getSourceFile(fileName))];
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
      return this.astFactory.createSourceSet(fileName, this.createSourceSet(fileName));
    });

    let sourceSetBundle = new declarations.SourceBundleDeclarationProto();

    let libRootUid = "<LIBROOT>";

    this.declarationsVisitor.forEachDeclaration((nodes, resourceSource: RootNode) => {
      let resourceName = resourceSource.getSourceFile().fileName;
      let uid = this.declarationsVisitor.isLibDeclaration(resourceName) ? libRootUid : "<TRANSIENT>";

      nodes.forEach(v => console.log("NODE MAP ", v.getText().substring(0, 90)));
      let filterFunc = (node: ts.Node) => nodes.has(node);

      let modules: any[] = [];
      if (ts.isSourceFile(resourceSource)) {
        modules = [this.astConverter.createModuleFromSourceFile(resourceSource, filterFunc)]
      } else if (ts.isModuleDeclaration(resourceSource)) {
        modules = this.astConverter.convertModule(resourceSource, filterFunc)
      }

      let files = modules.map(moduleDeclaration => {
        return this.astFactory.createSourceFileDeclaration(
          resourceName, moduleDeclaration  as any
        )
      });

      console.log(`FILES ${ts.SyntaxKind[resourceSource.kind]} ${resourceSource.getSourceFile().fileName} => ${this.astConverter.convertTopLevelStatement(resourceSource)} => ${files.length}`);

      sourceSets.push(this.astFactory.createSourceSet(resourceName, files));
    });

    sourceSetBundle.setSourcesList(sourceSets);
    return sourceSetBundle;
  }
}

export function createBundle(stdlib: string, files: Array<string>) {
  return new SourceBundleBuilder(stdlib, files).createBundle();
}