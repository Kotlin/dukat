
class C {
    foo() {
        return "text"
    }
}

var o = new C()

var str = o.foo()

module.exports.C = C
module.exports.o = o
module.exports.str = str