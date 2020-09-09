declare namespace API {
    interface EmitterA { }
    interface EmitterB { }

    interface Domain  {
        add(emitter: EmitterA | EmitterB): void;
        remove(emitter: EmitterA | EmitterB): void;
    }

    class SubDomain implements API.Domain {
        add(emitter: API.EmitterA | API.EmitterB): void;
        remove(emitter: API.EmitterA | API.EmitterB): void;
    }
}
