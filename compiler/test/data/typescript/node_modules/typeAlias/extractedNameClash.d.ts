declare interface ICompiler {
}

declare namespace ICompiler {
  type Handler = () => void;
}

declare class Compiler implements ICompiler {
  ping(): Compiler.Handler;
}

declare namespace Compiler {
  type Handler = ICompiler.Handler;
}
