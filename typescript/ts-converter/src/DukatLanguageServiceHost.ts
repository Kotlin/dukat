import * as ts from "typescript-services-api";
import {FileResolver} from "./FileResolver";
import {createLogger} from "./Logger";

export class DukatLanguageServiceHost implements ts.LanguageServiceHost {

    private static log = createLogger("DukatLanguageServiceHost");

    constructor(
        public fileResolver: FileResolver,
        private defaultLib: string,
        private knownFiles = new Set<string>(),
        private currentDirectory: string = "",
    ) {
    }

    getCompilationSettings(): ts.CompilerOptions {
        return ts.getDefaultCompilerOptions();
    }

    getScriptFileNames(): string[] {
        const res: string[] = [];

        this.knownFiles.forEach((v1, v2, s) => {
            res.push(v1);
        });

        return res;
    }

    getScriptVersion(fileName: string): string {
        return "0";
    }

    getDefaultLibFileName(options: ts.CompilerOptions): string {
        return this.defaultLib;
    }

    getCurrentDirectory(): string {
        return this.currentDirectory;
    }

    getScriptSnapshot(fileName: string): ts.IScriptSnapshot | undefined {
        const contents = this.fileResolver.resolve(fileName);
        return ts.ScriptSnapshot.fromString(contents);
    }

    log(message: string): void {
        DukatLanguageServiceHost.log.debug(message);
    }

    register(knownFile: string) {
        this.knownFiles.add(knownFile);
    }
}