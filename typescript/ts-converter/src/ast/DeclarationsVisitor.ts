import * as ts from "typescript";
import {Declaration} from "./ast";

export class DeclarationsVisitor {

    private processed = new Set<ts.Node>();
    private declarations = new Map<string, Array<Declaration>>();

    private skipTypes = new Set([
        "Array",
        "Boolean",
        "Error",
        "Function",
        "Number",
        "RegExp",
        "ReadonlyArray",
        "String"
    ]);

    constructor(
        private typeChecker: ts.TypeChecker,
        private libsSet: Set<string>,
        private files: Set<string>,
        public createDeclarations?: (node: ts.Node) => Array<Declaration>
    ) {
    }

    private registerDeclaration(declaration) {
        if (this.processed.has(declaration)) {
            return;
        }

        this.processed.add(declaration);

        const sourceName = declaration.getSourceFile().fileName;

        if (!Array.isArray(this.declarations.get(sourceName))) {
            this.declarations.set(sourceName, []);
        }

        if (this.createDeclarations) {
            this.declarations.get(sourceName)!.push(...this.createDeclarations(declaration));
        }
    }

    private checkReferences(node: ts.Node) {
        const symbol = this.typeChecker.getTypeAtLocation(node).symbol;
        if (symbol && Array.isArray(symbol.declarations)) {
            for (let declaration of symbol.declarations) {
                if (this.isTransientDependency(declaration)) {
                    this.registerDeclaration(declaration);
                }
            }
        }
    }

    process(node: ts.TypeNode) {
        const shouldNotBeProcessed = ts.isTypeReferenceNode(node) && this.skipTypes.has(node.typeName.getText());
        if (!shouldNotBeProcessed) {
            this.checkReferences(node)
        }
    }

    public forEachDeclaration(callback: (value: Array<ts.Node>, key: string) => void) {
        this.declarations.forEach(callback);
    }

    isTransientDependency(node: string | ts.Node): boolean {
        const fileName = (typeof node == "string") ? node : node.getSourceFile().fileName;
        return !this.files.has(fileName);
    }

    isLibDeclaration(source: string | ts.Node): boolean {
        const fileName = (typeof source == "string") ? source : source.getSourceFile().fileName;
        return this.libsSet.has(fileName);
    }

}