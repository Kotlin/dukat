import * as transformable from "_transformable";

declare interface Cipher {}

declare function createCipher(algorithm: string, options?: transformable.TransformOptions): Cipher;


