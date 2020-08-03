export interface MODULE_KINDMap {
    A: 0;
    B: "b";
    C: 1;
    D: "d";
}

export let x: MODULE_KINDMap["A" | "C"];
export let y: MODULE_KINDMap["B" | "D"];