type Benc = "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex";

interface RS {
  unshift(chunk: string | Array<string>, encoding?: Benc): void;
}

declare class R implements RS {
  unshift(chunk: any, encoding?: Benc): void;
}
