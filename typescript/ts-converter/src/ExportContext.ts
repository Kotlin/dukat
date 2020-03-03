import {uid} from "./uid";
import * as ts from "typescript";
import {createLogger} from "./Logger";


function resolveName(node: ts.Node): string | null {
  if (ts.isIdentifier(node)) {
    return node.text;
  }

  if (node.name && ts.isIdentifier(node.name)) {
    return node.name.text;
  }

  return null;
}

export class ExportContext {
  private exportTable: Map<ts.Node, string> = new Map();
  private log = createLogger("ExportContext");

  constructor(private isLibNode: (node: ts.Node) => boolean){}


  getUID(node: ts.Node): string {
    if (!this.exportTable.has(node)) {
      let nodeUid = uid();
      if (this.isLibNode(node)) {
        nodeUid = "lib-" + nodeUid;
      }

      let name = resolveName(node);
      if (name) {
        nodeUid = nodeUid + `${ts.SyntaxKind[node.kind]}-${name}`;
      }

      this.exportTable.set(node, nodeUid);
    }
    return this.exportTable.get(node) || "";
  }
}