interface S<D, T extends (() => S<D>) = () => S<D>> {}

interface Chain<T, D = T> {}
interface ArrayChain<G> extends Chain<Array<G>> {}

interface Component<S, T> {}

interface FrameworkElement<P = any, T extends string | ElementConstructor<any> = string | ElementConstructor<any>> {
  type: T;
  props: P;
}

type ElementConstructor<P> =
  | ((props: P) => FrameworkElement | null)
  | (new (props: P) => Component<P, any>);