dictionary WebGLContextAttributes {
    boolean alpha = true;
    boolean depth = true;
    boolean stencil = false;
    boolean antialias = true;
    boolean premultipliedAlpha = true;
    boolean preserveDrawingBuffer = false;
    boolean preferLowPowerToHighPerformance = false;
    boolean failIfMajorPerformanceCaveat = false;
};

dictionary PointerEventInit {
    long pointerId = 0;
    double width = 1;
    double height = 1;
    float pressure = 0;
    float tangentialPressure = 0;
    long tiltX = 0;
    long tiltY = 0;
    long twist = 0;
    DOMString pointerType = "";
    boolean isPrimary = false;
};

interface A {
    attribute WebGLContextAttributes attributes;
};

dictionary UndefinedMemberDictionary {
    required DOMString str;
    DOMString str2;
};

dictionary NullableMemberDictionary {
    long? value1 = 0;
    long? value2 = null;
};