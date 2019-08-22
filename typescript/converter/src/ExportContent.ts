import {uid} from "./uid";

export class ExportContent<T> {
  constructor(private exportTable: Map<T, string> = new Map()){}

  private createKey(node: T): T {
    return node;
  }

  getUID(node: T): string {
    let nodeKey = this.createKey(node);
    if (!this.exportTable.has(node)) {
      this.exportTable.set(node, uid());
    }
    return this.exportTable.get(node) || "";
  }
}

export function createExportContent(): ExportContent<any> {
  return new ExportContent<any>();
}
