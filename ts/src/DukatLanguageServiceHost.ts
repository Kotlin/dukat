/// <reference path="../build/package/node_modules/typescript/lib/tsserverlibrary.d.ts"/>

class DukatLanguageServiceHost implements ts.LanguageServiceHost {

    constructor(
        public fileResolver: FileResolver,
        private knownFiles = new Set<string>(),
        private currentDirectory: string = ""
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
        });

        return res;
    }

    getScriptVersion(fileName: string): string {
        return "0";
    }

    getDefaultLibFileName(options: ts.CompilerOptions): string {
        return "lib.d.ts";
    }

    getCurrentDirectory(): string {
        return this.currentDirectory;
    }

    getScriptSnapshot(fileName: string): ts.IScriptSnapshot | undefined {
        try {
            var contents = this.fileResolver.resolve(fileName);
            return ts.ScriptSnapshot.fromString(contents);
        } catch (e) {
            // TODO: need to pass error logger from frontend here
        }
    }

    log(message: string): void {
    }

    register(knownFile: string) {
        this.knownFiles.add(knownFile);
    }
}