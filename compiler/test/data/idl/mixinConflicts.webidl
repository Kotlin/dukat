interface PerformanceX {

};

interface mixin WindowOrWorkerGlobalScope {
    readonly attribute PerformanceX performance;
};

interface WindowX {
    attribute PerformanceX performance;
};

WindowX includes WindowOrWorkerGlobalScope;

interface SVGAnimatedStringX {

};

interface SVGAElementX {

};

interface mixin SVGURIReference {
    readonly attribute SVGAnimatedStringX href;
};

interface mixin HTMLHyperlinkElementUtils {
    stringifier attribute USVString href;
};

SVGAElementX includes SVGURIReference;
SVGAElementX includes HTMLHyperlinkElementUtils;
