interface A {
    void f(EventListener eventListener, StringListener stringListener);
};

callback interface EventListener {
    void handleEvent(Event event);
};

callback interface StringListener {
    DOMString handleString(DOMString string);
};

interface Event {

};