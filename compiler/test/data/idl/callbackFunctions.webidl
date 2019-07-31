interface A {
    attribute ACallback event1;
    attribute ACallback? event2;
};

callback ACallback = void (A? a, long b);