import * as transformable from "_transformable";
import {Computable} from "_computable";

declare interface Cipher {}

declare function createCipher(algorithm: string, options?: transformable.TransformOptions): Cipher;
declare function createComputable(): Computable;

