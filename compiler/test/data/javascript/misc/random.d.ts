let _ = {}

// ???
// Retrieve the values of an object's properties.
// Array<any>
function values(obj) {
    var keys = _.keys(obj);
    var length = keys.length;
    var values = Array(length);
    for (var i = 0; i < length; i++) {
        values[i] = obj[keys[i]];
    }
    return values;
}
_.values = values

// Retrieve the names of an object's own properties.
// Delegates to **ECMAScript 5**'s native Object.keys.
function keys(obj) {
    if (!_.isObject(obj)) return [];
    if (nativeKeys) return nativeKeys(obj);
    var keys = [];
    for (var key in obj) if (has(obj, key)) keys.push(key);
    // Ahem, IE < 9.
    if (hasEnumBug) collectNonEnumProps(obj, keys);
    return keys;
}
_.keys = keys

function negate(predicate) {
    return !predicate.apply(this, arguments);
}
_.negate = negate

// Is a given value an array?
// Delegates to ECMA5's native Array.isArray
function isArray(obj) {
    return toString.call(obj) === '[object Array]';
}
_.isArray = isArray

// Is a given variable an object?
function isObject(obj) {
    var type = typeof obj;
    return type === 'function' || type === 'object' && !!obj;
}
_.isObject = isObject

// Is a given value a DOM element?
function isElement(obj) {
    return !!(obj && obj.nodeType === 1);
}
_.isElement = isElement

// Is a given array, string, or object empty?
// An "empty" object has no enumerable own-properties.
function isEmpty(obj) {
    if (obj == null) return true;
    if (isArrayLike(obj) && (_.isArray(obj) || _.isString(obj) || _.isArguments(obj))) return obj.length === 0;
    return _.keys(obj).length === 0;
}
_.isEmpty = isEmpty

// Internal pick helper function to determine if obj has key key.
function keyInObj(value, key, obj) {
    return key in obj;
}

// Generator function to create the findIndex and findLastIndex functions.
function createPredicateIndexFinder(array, predicate, context, dir) {
    predicate = cb(predicate, context);
    var length = getLength(array);
    var index = dir > 0 ? 0 : length - 1;
    for (; index >= 0 && index < length; index += dir) {
        if (predicate(array[index], index, array)) return index;
    }
    return -1;
}

function isArrayLike(collection) {
    var length = getLength(collection);
    return typeof length == 'number' && length >= 0 && length <= MAX_ARRAY_INDEX;
}