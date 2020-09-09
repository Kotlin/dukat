interface A<T> { }
interface B<T> { }

interface Api {
    check<T>(values: Array<T>): T
    check<T>(values: Array<A<T> | B<T>>): T
}