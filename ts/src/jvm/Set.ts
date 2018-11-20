declare var java: any;
declare var Java: any;

class Set<T> {
  readonly size: number = 0;
  private hashSet = new java.util.HashSet();

  add(value: T): this {
    this.hashSet.add(value);
    return this;
  };

  clear() {
    this.hashSet.clear();
  }

  delete(value: T): boolean {
    return this.hashSet.remove(value);
  }

  has(value: T) {
    return this.hashSet.contains(value);
  }

  forEach(callbackfn: (value: T, value2: T, set: Set<T>) => void, thisArg?: any): void {
    var iter = this.hashSet.iterator();
    while (iter.hasNext()) {
      let value = iter.next();
      callbackfn(value, value, this)
    }
  }

}
