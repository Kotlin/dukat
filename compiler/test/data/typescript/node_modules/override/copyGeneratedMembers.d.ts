type Enc = "ascii" | "utf8" | "utf-8" | "utf16le" | "ucs2" | "ucs-2" | "base64" | "latin1" | "binary" | "hex";
interface RS {
  unshift(chunk: string | Uint8Array | Array<number>, encoding?: Enc): void;
  shouldNotBeCopied(a: String | Array<String>): Array<String>
}
declare class R implements RS {
  unshift(chunk: any, encoding?: Enc): void;
  shouldNotBeCopied(a: String | Array<String>): Array<String>
}

declare class X implements R {
  shouldNotBeCopied(a: String | Array<String>): Array<String>;
  unshift(chunk: any, encoding?: Enc): void;
}