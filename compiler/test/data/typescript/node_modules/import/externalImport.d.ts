import _ = require("./_core");

declare module "./_core" {
  interface Static {
    getVersion(): string;
  }
}