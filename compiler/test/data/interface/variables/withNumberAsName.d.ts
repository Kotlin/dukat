declare interface IBar {}

declare interface IFoo {
    200: string;
    300?: number;
    400?: IBar;
}