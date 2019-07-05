// based on sinon.d.ts
interface SinonStatic {
    createStubInstance: (constructor: any) => SinonStub;
	format: (obj: any) => string;
	log: (message: string) => void;
    restore(object: any): void;
}

declare var sinon: SinonStatic;

declare module "sinon" {
    export = sinon;
}
