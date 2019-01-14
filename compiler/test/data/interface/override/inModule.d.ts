declare module TypeScript {
    interface ISyntaxElement {
        childAt(index: number): ISyntaxElement;
    }

    interface ISeparatedSyntaxList extends ISyntaxElement {
        childAt(index: number): ISeparatedSyntaxList;
    }
}
