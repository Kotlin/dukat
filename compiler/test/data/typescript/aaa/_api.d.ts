declare module "api" {
    interface API {
        ping(): boolean;
    }

    export = API
}