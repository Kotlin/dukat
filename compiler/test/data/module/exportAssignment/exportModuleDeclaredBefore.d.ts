// based on semver.d.ts
declare module SemverModule {
    export function dummy();
}

//TODO: drop empty module
declare module "semver" {
    export = SemverModule;
}
