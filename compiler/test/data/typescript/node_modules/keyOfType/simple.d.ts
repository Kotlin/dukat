export interface MODULE_KINDMap {
    DECLARATION_FILE: 0;
    SOURCE_FILE: 1;
    AMBIENT_MODULE: 2;
    NAMESPACE: 3;
}

export let x: keyof MODULE_KINDMap;
export let y: MODULE_KINDMap[keyof MODULE_KINDMap]