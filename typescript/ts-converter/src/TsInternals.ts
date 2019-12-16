import * as ts from "typescript";

//Declarations that declared inside namespace marked as internal and not exist inside typescriptServices.d.ts and typescript.d.ts, but available at runtime
interface TsInternals {
  normalizePath(path: string): string;

  getDirectoryPath(path: string): string;

  libMap: { get(path: string): string };
}

export let tsInternals = ((ts as any) as TsInternals);