export interface Low {
    p: String
}

export interface Some extends Low {
    prop: string
}

export function usePartial(some: Some, partial_some: Partial<Some>);