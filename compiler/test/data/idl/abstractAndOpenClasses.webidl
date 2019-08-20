[NoInterfaceObject]
interface ShouldBeInterface {
    attribute long a;
    readonly attribute long b;
};

interface ShouldBeAbstract {
    attribute long a;
    readonly attribute long b;
};

[Constructor]
interface ShouldBeOpen {
    attribute long a;
    readonly attribute long b;
};

interface ShouldBeOpenToo : ShouldBeOpen {
    attribute long c;
    readonly attribute long d;
};