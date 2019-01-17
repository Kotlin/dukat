package simple

inline fun Event.foo(): Unit { this.asDynamic().foo() }
inline var Event.bar: Any get() = this.asDynamic().bar; set(value) { this.asDynamic().bar = value }
inline operator fun Event.get(prop: String): Number? { return this.asDynamic().get(prop) }
inline operator fun Event.set(prop: String, value: Number): Unit { this.asDynamic().set(prop, value) }
inline var Event.someField: String get() = this.asDynamic().someField; set(value) { this.asDynamic().someField = value }
inline var Event.optionalField: Any? get() = this.asDynamic().optionalField; set(value) { this.asDynamic().optionalField = value }
inline operator fun Event.invoke(resourceId: String, hash: Any? = null, callback: Function<*>? = null): Unit { this.asDynamic().invoke(resourceId, hash, callback) }
