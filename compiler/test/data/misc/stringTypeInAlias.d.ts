// based on https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/a11y-dialog/index.d.ts

type DialogEvents = "show" | "hide" | "destroy" | "create";
declare class A11yDialog {
    constructor(el: Element | null, containers?: NodeList | Element | string | null);
    create(el?: Element | null, containers?: NodeList | Element | string | null): void;
    on(evt: DialogEvents, callback: (dialogElement: any, event: Event) => void): void;
    off(evt: DialogEvents, callback: (dialogElement: any, event: Event) => void): void;
}

export = A11yDialog;
