import * as ts from "typescript";
import {createLogger} from "./Logger";
import {System} from "../.tsdeclarations/typescript";

export class DukatLanguageServiceHost implements ts.LanguageServiceHost {

    private static log = createLogger("DukatLanguageServiceHost");
    private fileResolver: System = ts.sys;

    constructor(
        private defaultLib: string,
        private knownFiles = new Set<string>(),
        private currentDirectory: string = "",
    ) {
    }

    getCompilationSettings(): ts.CompilerOptions {
        let compilerOptions = ts.getDefaultCompilerOptions();
        compilerOptions.allowJs = true;
        return compilerOptions;
    }

    getScriptFileNames(): string[] {
        return Array.from(this.knownFiles);
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
        const contents = this.fileResolver.readFile(fileName);
        return ts.ScriptSnapshot.fromString(contents);
    }

    log(message: string): void {
        DukatLanguageServiceHost.log.debug(message);
    }

    register(knownFile: string) {
        this.knownFiles.add(knownFile);
    }

    fileExists(filePath: string): boolean {
        return this.fileResolver.fileExists(filePath);
    }

    readDirectory(path: string, extensions?: readonly string[], exclude?: readonly string[], include?: readonly string[], depth?: number): string[] {
        return this.fileResolver.readDirectory(path, extensions, exclude, include, depth);
    }
}