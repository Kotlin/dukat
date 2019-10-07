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

enum LogLevel {
    FATAL = 0,
    ERROR = 1,
    WARN = 2,
    INFO = 3,
    DEBUG = 4,
    TRACE = 5
}

class SimpleLogger implements Logger {
    private commonPrefix = "[ts]";
    private logLevel = parseInt(process.env.DUKAT_LOGLEVEL!!, 10);

    static console_logger = (message: string) => {
        console.log(message);
    };

    constructor(private prefix: string, private output: (message: string) => void = SimpleLogger.console_logger) {}

    private logMessage(message: string, logDescriptor: string): string {
        return `${this.commonPrefix} [${logDescriptor}] [${this.prefix}] ${message}`;
    }

    private log(message: string): void {
        this.output(message);
    }

    private guardLog(message, logLevel: number, logDescriptor: string) {
        if (logLevel <= this.logLevel) {
            this.log(this.logMessage(message, logDescriptor));
        }
    }

    debug(message: string): void {
        this.guardLog(message, LogLevel.DEBUG, "DEBUG");
    }

    info(message: string): void {
        this.guardLog(message, LogLevel.INFO, "INFO");
    }

    trace(message: string): void {
        this.guardLog(message, LogLevel.TRACE, "TRACE");
    }

    warn(message: string): void {
        this.guardLog(message, LogLevel.WARN, "WARN");
    }
}

export function createLogger(name: string, output: (message: string) => void = SimpleLogger.console_logger): Logger {
    return new SimpleLogger(name, output);
}