// based on semver.d.ts

//TODO: drop empty module
declare module "semver" {
    export = SemverModule;
}

declare module SemverModule {
    export function dummy();
}
