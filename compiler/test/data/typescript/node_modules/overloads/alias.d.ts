interface MockjsRandomBasic {
    character(pool: 'lower' | 'upper' | 'number' | 'symbol'): S;
    character(pool?: S): S;
}

type S = string