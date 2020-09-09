export interface SchedulerLike {
    now(): number;
}

export declare class Scheduler implements SchedulerLike {
    now: () => number;
}