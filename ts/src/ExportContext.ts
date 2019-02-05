

class ExportContext {
    constructor(private exportTable: Map<ts.Node, string> = new Map()) {
    }

    getUID(node: ts.Node): string {
        let uidFound = this.exportTable.get(node);
        if (uidFound) {
            return uidFound;
        }

        let uidGenerated = uid();
        this.exportTable.set(node, uidGenerated);
        return uidGenerated;
    }
}