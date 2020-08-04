interface WritableStateOptions {
    write?(encoding: "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex" | string): void;
}

interface RealConflict {
    conflictMethod?(arg: string | number): void;
}