// based on ace.d.ts
declare module AceAjax {
    export interface KeyBinding {

        setDefaultHandler(kb);

        setKeyboardHandler(kb);

        addKeyboardHandler(kb, pos);

        removeKeyboardHandler(kb): boolean;

        getKeyboardHandler(): any;

        onCommandKey(e, hashId, keyCode);

        onTextInput(text);
    }
    export var KeyBinding: Foo;

    export interface Foo {
        foo(editor: Editor): boolean;
    }

    export interface Editor {

    }
}
