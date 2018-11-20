/// <reference path="../node_modules/typescript/lib/typescriptServices.d.ts"/>
/// <reference path="../node_modules/typescript/lib/tsserverlibrary.d.ts"/>

if (typeof ts == "undefined") {
  (global as any).ts = require("typescript/lib/tsserverlibrary");
}

declare function print(...arg: any[]): void;

if (typeof console == "undefined") {
  (global as any).console = {
    log: (...arg: any[]) => {
      print(arg);
    }
  }
}

import fromString = ts.ScriptSnapshot.fromString;

interface FileResolver {
  resolve(fileName: string): string;
}

class SomeLanguageServiceHost implements ts.LanguageServiceHost {

  constructor(
    public fileResolver: FileResolver,
    private knownFiles = new Set<string>(),
    private currentDirectory: string = "",
  ) {
  }

  getCompilationSettings(): ts.CompilerOptions {
    return ts.getDefaultCompilerOptions();
  }

  getScriptFileNames(): string[] {
    var res: string[] = [];

    this.knownFiles.forEach((v1, v2, s) => {
      let item = v1;
      res.push(item);
    })

    return res;
  }

  getScriptVersion(fileName: string): string {
    return "0";
  }

  getDefaultLibFileName(options: ts.CompilerOptions): string {
    return "./ts/node_modules/typescript/lib/lib.d.ts";
  }

  getCurrentDirectory(): string {
    return this.currentDirectory;
  }

  getScriptSnapshot(fileName: string): ts.IScriptSnapshot | undefined {
    var contents = this.fileResolver.resolve(fileName);
    return fromString(contents);
  }

  log(message: string): void {
    console.log(message);
  }

  register(knownFile: string) {
    this.knownFiles.add(knownFile);
  }
}



let main = (fileResolver: FileResolver) => {
  let documentRegistry = ts.createDocumentRegistry();
  let host= new SomeLanguageServiceHost(fileResolver);
  host.register("./ast/common/test/data/simplest_var.declarations.d.ts");

  let languageService = ts.createLanguageService(host, documentRegistry);


  var program = languageService.getProgram();

  if (program != null) {
    let fileName = "./ast/common/test/data/simplest_var.declarations.d.ts";
    var sourceFile = program.getSourceFile(fileName);

    if (sourceFile != null) {
      console.log(sourceFile.text);
    }
  }
}


if (typeof module != "undefined") {
  module.exports = main;
}
