export type StructureType = {
    name: string;
    details?: string;
};

export interface Registry {
    register(type: StructureType)
}
