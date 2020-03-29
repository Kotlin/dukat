declare function createQuasiSearchFunc(): QuasiSearchFunc;

interface SearchFunc {
  (source: string, subString: string): boolean;
}

type SearchSignature = (source: string, subString: string) => boolean;
interface QuasiSearchFunc extends SearchSignature {}