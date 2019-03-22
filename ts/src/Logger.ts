declare interface Logger {
    debug(message: string): void;
    info(message: string): void;
    warn(message: string): void;
    trace(message: string): void;
}

declare function createLogger(name: String): Logger;