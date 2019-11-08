class TestSet {
    isElement(obj) {
        return !!(obj && obj.nodeType === 1);
    }

    isObject(obj) {
        var type = typeof obj;
        return type === 'function' || type === 'object' && !!obj;
    }

    isArray(obj) {
        return toString.call(obj) === '[object Array]';
    }

    isArrayLike(collection) {
        var length = getLength(collection);
        return typeof length == 'number' && length >= 0 && length <= MAX_ARRAY_INDEX;
    }

    keyInObj(key, obj) {
        return key in obj;
    }

    //Originally returns a function (this was skipped for testing)
    negate(predicate) {
        return !predicate.apply(this, arguments);
    }
}

/*
// ???
// Retrieve the values of an object's properties.
// Array<any>
export _.values = function(obj) {
    var keys = _.keys(obj);
    var length = keys.length;
    var values = Array(length);
    for (var i = 0; i < length; i++) {
        values[i] = obj[keys[i]];
    }
    return values;
};
// Retrieve the names of an object's own properties.
// Delegates to **ECMAScript 5**'s native Object.keys.
_.keys = function(obj) {
    if (!_.isObject(obj)) return [];
    if (nativeKeys) return nativeKeys(obj);
    var keys = [];
    for (var key in obj) if (has(obj, key)) keys.push(key);
    // Ahem, IE < 9.
    if (hasEnumBug) collectNonEnumProps(obj, keys);
    return keys;
};
// Generator function to create the findIndex and findLastIndex functions.
var createPredicateIndexFinder = function(dir) {
    return function(array, predicate, context) {
        predicate = cb(predicate, context);
        var length = getLength(array);
        var index = dir > 0 ? 0 : length - 1;
        for (; index >= 0 && index < length; index += dir) {
            if (predicate(array[index], index, array)) return index;
        }
        return -1;
    };
};
_.negate = function(predicate) {
    return function() {
        return !predicate.apply(this, arguments);
    };
};
// Is a given array, string, or object empty?
// An "empty" object has no enumerable own-properties.
_.isEmpty = function(obj) {
    if (obj == null) return true; isEmpty1 => boolean
    if (isArrayLike(obj) && (.isArray(obj) || .isString(obj) || _.isArguments(obj))) return obj.length === 0; isEmpty2 => boolean
    return _.keys(obj).length === 0; isEmpty3 => boolean
};
*/