export class DocumentCache {
  private myDocumentMap: Map<string, any> = new Map();

  setDocument(key: string, path: string, sourceFile: any) {
    this.myDocumentMap.set(path, sourceFile);
  }

  getDocument(key: string, path: string): any | undefined {
    return this.myDocumentMap.get(path);
  }
}
