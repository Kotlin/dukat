//TODO: incomplete

interface SomethingElse<B> {
  bing(): Component<B, number>
}

declare class Component<A, B> {
  ping();
}


interface Component<A = {}, B = {}, C = any> extends SomethingElse<B> {
  pong();
}

declare function getComponent<A, B, C>(): Component<A>