import {uid} from "./uid";
import * as ts from "typescript-services-api";

export class ExportContext {
  private exportTable: Map<ts.Node, string> = new Map();

  constructor(private isLibNode: (node: ts.Node) => boolean){}


  getUID(node: ts.Node): string {
    if (!this.exportTable.has(node)) {
      let nodeUid = uid();
      if (this.isLibNode(node)) {
        nodeUid = "lib-" + nodeUid;
      }

      this.exportTable.set(node, nodeUid);
    }
    return this.exportTable.get(node) || "";
  }
}

export function createExportContext(isLibNode: (node: ts.Node) => boolean): ExportContext {
  return new ExportContext(isLibNode);
}
