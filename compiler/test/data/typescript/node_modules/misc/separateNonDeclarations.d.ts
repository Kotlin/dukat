declare module "api" {
  interface Api {}
  type App = Api;
  function createApp(): App;
}