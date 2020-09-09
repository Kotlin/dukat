interface MapLike<K, V> {
}
interface List<T> {
}

type Values<V> = List<V>;
type MultiMap<K,V> = MapLike<K,Values<V>>;
type MyHeaders = MultiMap<String,String>
type Ref<T> = string | ((instance: T) => any);
type Ping<T> = (packet: {ip: T}) => boolean;

declare var fooMap: MultiMap<string,number>;

declare function mapKey(a: MultiMap<number,String>);

declare var fooStringOrMap: string | MultiMap<string,number>;

declare function stringOrMapKey(a: string | MultiMap<number,String>);

declare var listOfStringOrNumber: string | List<string | number>;

declare function listOfNumberOrString(a: List<number | String>);

declare var headers: MyHeaders;

declare function getHeaders(): MyHeaders;

declare function addHeaders(headers: MyHeaders);

declare var someRef: Ref<number>;

declare function addRef(ref: Ref<number>);

interface SomeInterface {
  cached: {
    <T extends (...args: any[]) => any>(func: T, resolver?: (...args: any[]) => any): T;
  };
}