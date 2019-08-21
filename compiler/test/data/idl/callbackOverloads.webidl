interface A {
    void f(EventListenerX eventListener, StringListenerX stringListener);
};

callback interface EventListenerX {
    void handleEvent(EventX event);
};

callback interface StringListenerX {
    DOMString handleString(DOMString string);
};

interface EventX {

};