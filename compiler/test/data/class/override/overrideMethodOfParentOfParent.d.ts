declare module TypeScript.Syntax {
    // TODO: generate right refrence to ISyntaxList
    class EmptySyntaxList implements ISyntaxList {
        public kind(): SyntaxKind;
    }
}

declare module TypeScript {
    interface ISyntaxElement {
        kind(): SyntaxKind;
    }
}

declare module TypeScript {
    interface ISyntaxList extends ISyntaxElement {
        childAt(index: number): ISyntaxNodeOrToken;
        toArray(): ISyntaxNodeOrToken[];
        insertChildrenInto(array: ISyntaxElement[], index: number): void;
    }
}

