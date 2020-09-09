interface API {
  pong(b: string): boolean
  pong(c: number): boolean
  foo(a: string | number, b?: boolean)
}