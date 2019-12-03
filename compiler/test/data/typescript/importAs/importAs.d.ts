import * as transformable from "_transformable";
import {Pipable, Computable} from "_computable";

declare interface Cipher {
  getPipable(): Pipable
}

declare function createCipher(algorithm: string, options?: transformable.TransformOptions): Cipher;
declare function createComputable(): Computable;
declare function createPipable(): Pipable;

