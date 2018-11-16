declare class AstNode { }

declare class AstTree {
 root: AstNode;
}

declare class JavaList<T> {
  add(elem: T): void;
}

let ping = (arr: JavaList<number>) => {
    arr.add(1);
}
