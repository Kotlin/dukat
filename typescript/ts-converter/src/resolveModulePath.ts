import {tsInternals} from "./TsInternals";
import * as ts from "typescript";

export function resolveModulePath(node: ts.Expression): string | null {
  const module = ts.getResolvedModule(node.getSourceFile(), node.text);
  if (module && (typeof module.resolvedFileName == "string")) {
    return tsInternals.normalizePath(module.resolvedFileName);
  }
  return null;
}