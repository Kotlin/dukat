import {A as RenamedA} from "./_importedAPI"

interface A extends RenamedA {
    pong(): boolean;
    copy(): A;
}

declare class B implements A {
    ping(): boolean;
    pong(): boolean;
    copy(): A;
}
