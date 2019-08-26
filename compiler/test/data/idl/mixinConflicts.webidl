interface PerformanceX {

};

interface mixin WindowOrWorkerGlobalScope {
    readonly attribute PerformanceX performance;
};

interface WindowX {
    readonly attribute PerformanceX performance;
};

WindowX includes WindowOrWorkerGlobalScope;