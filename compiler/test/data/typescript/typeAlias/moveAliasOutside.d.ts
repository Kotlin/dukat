declare module "something" {
  export type WithSource = String

  export function ping(source: WithSource): void;
}
