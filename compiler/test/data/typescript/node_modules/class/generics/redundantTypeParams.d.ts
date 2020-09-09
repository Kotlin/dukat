interface MyPromiseLike<T>{}
interface MyPromise<T>{}

interface MyPromiseConstructor {
  ping<T>(a: T)
  pong<T>(): T
  bang<Y, Z>(condition: (y: Y) => Boolean): () => Z
  all<T1, T2>(values: [T1 | MyPromiseLike<T1>, T2 | MyPromiseLike<T2>]): MyPromise<[T1, T2]>;
  all<T1, T2, T3>(values: [T1 | MyPromiseLike<T1>, T2 | MyPromiseLike<T2>, T3 | MyPromiseLike<T3>]): MyPromise<[T1, T2, T3]>;
}