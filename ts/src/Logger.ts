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

    constructor(private prefix: string) {}

    private logMessage(message: string): string {
        return `${this.commonPrefix} [${this.prefix}] ${message}`;
    }

    debug(message: string): void {
        console.log(this.logMessage(message));
    }

    info(message: string): void {
        console.log(this.logMessage(message));
    }

    trace(message: string): void {
        console.log(this.logMessage(message));
    }

    warn(message: string): void {
        console.log(this.logMessage(message));
    }
}

export function createLogger(name: string): Logger {
    return new SimpleLogger(name);
}