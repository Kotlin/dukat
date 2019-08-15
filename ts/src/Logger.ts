interface Logger {
    debug(message: string): void;
    info(message: string): void;
    warn(message: string): void;
    trace(message: string): void;
}

interface ConsoleLogger {
    log(message: string): string;
}

declare var console: ConsoleLogger;

class SimpleLogger implements Logger {
    private commonPrefix = "[ts]";

    static dummy_logger = (message: string) => {};

    constructor(private prefix: string, private output: (message: string) => void = SimpleLogger.dummy_logger) {}

    private logMessage(message: string): string {
        return `${this.commonPrefix} [${this.prefix}] ${message}`;
    }

    private log(message: string): void {
        if (this.output !== SimpleLogger.dummy_logger) {
            this.output(message);
        }
    }

    debug(message: string): void {
        this.log(this.logMessage(message));
    }

    info(message: string): void {
        this.log(this.logMessage(message));
    }

    trace(message: string): void {
        this.log(this.logMessage(message));
    }

    warn(message: string): void {
        this.log(this.logMessage(message));
    }
}

export function createLogger(name: string, output: (message: string) => void = SimpleLogger.dummy_logger): Logger {
    return new SimpleLogger(name, output);
}