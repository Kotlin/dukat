interface Map<K, V> {
}
interface List<T> {
}

type Values<V> = List<V>;
type ListOrMultiMap<K,V> = List<V> | Map<K,Values<V>>;

declare var aliasUnionVar: ListOrMultiMap<string,number>;

declare function aliasUnionFunction(a: ListOrMultiMap<number,String>);

declare var listOfUnionVar: Values<string | number>;

declare function listOfUnionFunction(a: Values<number | String>);
