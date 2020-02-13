interface SymbolConstructor {
  readonly iterator: symbol;
  readonly asyncIterator: symbol;
}
declare var Symbol: SymbolConstructor;