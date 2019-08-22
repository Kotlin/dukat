interface A {
    attribute ACallback event1;
    attribute ACallback? event2;
    attribute AnyCallback event3;
};

callback ACallback = void (A? a, long b);
callback AnyCallback = any? ();