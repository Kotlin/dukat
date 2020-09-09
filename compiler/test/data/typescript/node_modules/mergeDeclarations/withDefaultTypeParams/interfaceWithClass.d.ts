//TODO: incomplete

interface SomethingElse<B> {
  bing(): Component<B, number>
}

interface Component<A = {}, B = {}, C = any> extends SomethingElse<B> {
  pong();
}

declare class Component<A, B> {
  ping();
}

declare function getComponent<A, B, C>(): Component<A>