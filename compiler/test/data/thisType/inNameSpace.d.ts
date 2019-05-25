declare namespace app {
    interface Status {
        current: number;
        total: number;
        type: "begin" | "end" | "unknown";
        status: "ok" | "fail";
    }

    interface EventEmitter {
        on(event: "customevent", listener: (info: Status) => void): this;
    }
}

declare function app(): app.EventEmitter;

export = app;
